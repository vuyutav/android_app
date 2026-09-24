using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using Microsoft.IdentityModel.Tokens;
using System;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using System.Threading.Tasks;
using SchoolMinimarket.Backend.Models;
using Microsoft.EntityFrameworkCore;
using BCrypt.Net;

namespace SchoolMinimarket.Backend.Controllers;

[ApiController]
[Route("api/v1/auth")]
public class AuthController : ControllerBase
{
    private readonly SchoolMinimarketContext _dbContext;
    private readonly IConfiguration _config;
    private readonly IJwtService _jwtService;

    public AuthController(SchoolMinimarketContext dbContext, IConfiguration config, IJwtService jwtService)
    {
        _dbContext = dbContext;
        _config = config;
        _jwtService = jwtService;
    }

    public record LoginRequest(string Email, string? NISN, string Password);
    public record LoginResponse(string AccessToken, string RefreshToken, Guid UserId, string Role);

    [HttpPost("login")]
    public async Task<ActionResult<LoginResponse>> Login([FromBody] LoginRequest request)
    {
        // Find user by email or NISN (if provided)
        var user = await _dbContext.Users
            .Include(u => u.Role)
            .FirstOrDefaultAsync(u =>
                (!string.IsNullOrEmpty(request.Email) && u.Email == request.Email) ||
                (!string.IsNullOrEmpty(request.NISN) && u.NISN == request.NISN));

        if (user == null)
            return Unauthorized(new { error = "User not found" });

        // Verify password (hash stored in PasswordHash)
        if (!BCrypt.Verify(request.Password, user.PasswordHash))
            return Unauthorized(new { error = "Invalid credentials" });

        var accessToken = _jwtService.GenerateToken(user);
        var refreshToken = _jwtService.GenerateRefreshToken(); // Simple GUID base64 token

        // In a real implementation you would persist the refresh token hashed.
        return Ok(new LoginResponse(accessToken, refreshToken, user.Id, user.Role?.Name ?? "Student"));
    }
}

// Simple JWT service (registered in Program.cs via DI placeholder)
public interface IJwtService
{
    string GenerateToken(User user);
    string GenerateRefreshToken();
}

public class JwtService : IJwtService
{
    private readonly IConfiguration _config;
    public JwtService(IConfiguration config) => _config = config;

    public string GenerateToken(User user)
    {
        var jwtSecret = _config["Jwt:Secret"] ?? "SuperSecretKeyForDevOnlyChangeInProd";
        var key = new SymmetricSecurityKey(Encoding.ASCII.GetBytes(jwtSecret));
        var creds = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);
        var expires = DateTime.UtcNow.AddMinutes(15);
        var claims = new[]
        {
            new Claim(JwtRegisteredClaimNames.Sub, user.Id.ToString()),
            new Claim(ClaimTypes.Role, user.Role?.Name ?? "Student"),
            new Claim("schoolId", user.SchoolId.ToString())
        };
        var token = new JwtSecurityToken(
            claims: claims,
            expires: expires,
            signingCredentials: creds);
        return new JwtSecurityTokenHandler().WriteToken(token);
    }

    public string GenerateRefreshToken() => Convert.ToBase64String(Guid.NewGuid().ToByteArray());
}

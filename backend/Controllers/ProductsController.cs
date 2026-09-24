using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using SchoolMinimarket.Backend.Models;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SchoolMinimarket.Backend.Controllers;

[ApiController]
[Route("api/v1/[controller]")]
[Authorize]
public class ProductsController : ControllerBase
{
    private readonly SchoolMinimarketContext _db;
    public ProductsController(SchoolMinimarketContext db) => _db = db;

    public record ProductDto(
        Guid Id,
        string Name,
        string? Description,
        decimal Price,
        bool IsActive,
        int Stock,
        Guid CategoryId,
        string CategoryName);

    // GET: api/v1/products?search=...&categoryId=...&page=1&size=20
    [HttpGet]
    public async Task<ActionResult<PagedResult<ProductDto>>> GetProducts(
        [FromQuery] string? search,
        [FromQuery] Guid? categoryId,
        [FromQuery] int page = 1,
        [FromQuery] int size = 20)
    {
        var query = _db.Products
            .Include(p => p.Category)
            .Include(p => p.Inventories)
            .AsNoTracking()
            .Where(p => p.IsActive);

        if (!string.IsNullOrWhiteSpace(search))
        {
            query = query.Where(p => p.Name.Contains(search));
        }
        if (categoryId.HasValue)
        {
            query = query.Where(p => p.CategoryId == categoryId.Value);
        }

        var total = await query.CountAsync();
        var items = await query
            .OrderBy(p => p.Name)
            .Skip((page - 1) * size)
            .Take(size)
            .Select(p => new ProductDto(
                p.Id,
                p.Name,
                p.Description,
                p.Price,
                p.IsActive,
                p.Inventories.FirstOrDefault(i => i.SchoolId == p.SchoolId).Quantity,
                p.CategoryId,
                p.Category != null ? p.Category.Name : string.Empty))
            .ToListAsync();

        var result = new PagedResult<ProductDto>
        {
            Page = page,
            Size = size,
            Total = total,
            Items = items
        };
        return Ok(result);
    }

    // GET: api/v1/products/{id}
    [HttpGet("{id:guid}")]
    public async Task<ActionResult<ProductDto>> GetProduct(Guid id)
    {
        var product = await _db.Products
            .Include(p => p.Category)
            .Include(p => p.Inventories)
            .AsNoTracking()
            .FirstOrDefaultAsync(p => p.Id == id && p.IsActive);

        if (product == null) return NotFound();

        var stock = product.Inventories.FirstOrDefault(i => i.SchoolId == product.SchoolId)?.Quantity ?? 0;
        var dto = new ProductDto(
            product.Id,
            product.Name,
            product.Description,
            product.Price,
            product.IsActive,
            stock,
            product.CategoryId,
            product.Category?.Name ?? string.Empty);
        return Ok(dto);
    }
}

// Simple pagination wrapper
public class PagedResult<T>
{
    public int Page { get; set; }
    public int Size { get; set; }
    public int Total { get; set; }
    public IEnumerable<T> Items { get; set; } = Enumerable.Empty<T>();
}

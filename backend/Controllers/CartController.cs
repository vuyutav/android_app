using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using SchoolMinimarket.Backend.Models;
using System;
using System.Linq;
using System.Threading.Tasks;

namespace SchoolMinimarket.Backend.Controllers;

[ApiController]
[Route("api/v1/[controller]")]
[Authorize]
public class CartController : ControllerBase
{
    private readonly SchoolMinimarketContext _db;
    public CartController(SchoolMinimarketContext db) => _db = db;

    // GET: api/v1/cart (current user's cart)
    [HttpGet]
    public async Task<ActionResult<Cart>> GetCart()
    {
        var userId = Guid.Parse(User.FindFirst("sub")?.Value ?? throw new InvalidOperationException("Missing sub claim"));
        var cart = await _db.Set<Cart>()
            .Include(c => c.Items)
                .ThenInclude(i => i.Product)
            .FirstOrDefaultAsync(c => c.UserId == userId);
        if (cart == null)
        {
            // create empty cart on demand
            cart = new Cart { Id = Guid.NewGuid(), UserId = userId, SchoolId = Guid.Parse(User.FindFirst("schoolId")?.Value ?? "0"), CreatedAt = DateTime.UtcNow, UpdatedAt = DateTime.UtcNow };
            _db.Add(cart);
            await _db.SaveChangesAsync();
        }
        return Ok(cart);
    }

    // POST: api/v1/cart (add or update item)
    public record AddItemRequest(Guid ProductId, int Quantity);
    [HttpPost]
    public async Task<ActionResult> AddOrUpdateItem([FromBody] AddItemRequest req)
    {
        var userId = Guid.Parse(User.FindFirst("sub")?.Value ?? throw new InvalidOperationException("Missing sub claim"));
        var cart = await _db.Set<Cart>()
            .Include(c => c.Items)
            .FirstOrDefaultAsync(c => c.UserId == userId);
        if (cart == null) return NotFound();

        var item = cart.Items.FirstOrDefault(i => i.ProductId == req.ProductId);
        if (item == null)
        {
            item = new CartItem { Id = Guid.NewGuid(), CartId = cart.Id, ProductId = req.ProductId, Quantity = req.Quantity };
            cart.Items.Add(item);
        }
        else
        {
            item.Quantity = req.Quantity;
        }
        cart.UpdatedAt = DateTime.UtcNow;
        await _db.SaveChangesAsync();
        return NoContent();
    }

    // DELETE: api/v1/cart/{productId}
    [HttpDelete("{productId:guid}")]
    public async Task<ActionResult> DeleteItem(Guid productId)
    {
        var userId = Guid.Parse(User.FindFirst("sub")?.Value ?? throw new InvalidOperationException("Missing sub claim"));
        var cart = await _db.Set<Cart>()
            .Include(c => c.Items)
            .FirstOrDefaultAsync(c => c.UserId == userId);
        if (cart == null) return NotFound();
        var item = cart.Items.FirstOrDefault(i => i.ProductId == productId);
        if (item != null)
        {
            cart.Items.Remove(item);
            _db.Remove(item);
            await _db.SaveChangesAsync();
        }
        return NoContent();
    }
}

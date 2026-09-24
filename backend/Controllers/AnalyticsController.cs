using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using SchoolMinimarket.Backend.Models;
using System;
using System.Linq;
using System.Threading.Tasks;

namespace SchoolMinimarket.Backend.Controllers;

[ApiController]
[Route("api/v1/admin/[controller]")]
[Authorize(Policy = "AdminOnly")]
public class AnalyticsController : ControllerBase
{
    private readonly SchoolMinimarketContext _db;
    public AnalyticsController(SchoolMinimarketContext db) => _db = db;

    // GET: api/v1/admin/analytics/daily-sales?date=2023-10-01
    [HttpGet("daily-sales")]
    public async Task<ActionResult<decimal>> GetDailySales([FromQuery] DateTime date)
    {
        var start = date.Date;
        var end = start.AddDays(1);
        var total = await _db.Orders
            .Where(o => o.CreatedAt >= start && o.CreatedAt < end && o.Status != "Dibatalkan")
            .SumAsync(o => (decimal?)o.TotalAmount) ?? 0m;
        return Ok(total);
    }

    // GET: api/v1/admin/analytics/top-products?date=2023-10-01&limit=5
    [HttpGet("top-products")]
    public async Task<ActionResult<object>> GetTopProducts([FromQuery] DateTime date, [FromQuery] int limit = 5)
    {
        var start = date.Date;
        var end = start.AddDays(1);
        var top = await _db.OrderItems
            .Where(oi => oi.Order.CreatedAt >= start && oi.Order.CreatedAt < end)
            .GroupBy(oi => oi.ProductId)
            .Select(g => new { ProductId = g.Key, Quantity = g.Sum(oi => oi.Quantity) })
            .OrderByDescending(x => x.Quantity)
            .Take(limit)
            .Join(_db.Products, pi => pi.ProductId, p => p.Id, (pi, p) => new { p.Name, pi.Quantity })
            .ToListAsync();
        return Ok(top);
    }

    // GET: api/v1/admin/analytics/low-stock?threshold=5
    [HttpGet("low-stock")]
    public async Task<ActionResult<object>> GetLowStock([FromQuery] int threshold = 5)
    {
        var low = await _db.Inventories
            .Where(i => i.Quantity <= threshold)
            .Select(i => new { i.ProductId, i.Quantity, ProductName = i.Product!.Name })
            .ToListAsync();
        return Ok(low);
    }
}

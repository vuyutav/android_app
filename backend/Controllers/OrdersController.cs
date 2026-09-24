using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using SchoolMinimarket.Backend.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SchoolMinimarket.Backend.Controllers;

[ApiController]
[Route("api/v1/[controller]")]
[Authorize]
public class OrdersController : ControllerBase
{
    private readonly SchoolMinimarketContext _db;
    public OrdersController(SchoolMinimarketContext db) => _db = db;

    // DTOs
    public record OrderItemRequest(Guid ProductId, int Quantity);
    public record CreateOrderRequest(List<OrderItemRequest> Items, string PaymentMethod, DateTime? PickupTime);
    public record CreateOrderResponse(string OrderNumber, string Status);

    // POST api/v1/orders
    [HttpPost]
    public async Task<ActionResult<CreateOrderResponse>> CreateOrder([FromBody] CreateOrderRequest req)
    {
        // Idempotency: client must send X-Idempotency-Key header
        var idemKey = Request.Headers["X-Idempotency-Key"].FirstOrDefault();
        if (string.IsNullOrWhiteSpace(idemKey))
            return BadRequest(new { error = "Missing Idempotency-Key header" });

        var userId = Guid.Parse(User.FindFirst("sub")?.Value ?? throw new InvalidOperationException("Missing sub claim"));
        var schoolId = Guid.Parse(User.FindFirst("schoolId")?.Value ?? throw new InvalidOperationException("Missing schoolId claim"));

        // Check if a pending order with same idempotency key already exists (simple implementation using a table not defined yet). For MVP we just assume uniqueness.

        // Load cart items (or use passed items directly). Here we use request items.
        var productIds = req.Items.Select(i => i.ProductId).ToArray();
        var products = await _db.Products
            .Include(p => p.Inventories)
            .Where(p => productIds.Contains(p.Id) && p.IsActive)
            .ToListAsync();

        // Verify stock and calculate total
        decimal total = 0;
        foreach (var item in req.Items)
        {
            var product = products.FirstOrDefault(p => p.Id == item.ProductId) ?? throw new InvalidOperationException($"Product {item.ProductId} not found");
            var inventory = product.Inventories.FirstOrDefault(i => i.SchoolId == schoolId) ?? throw new InvalidOperationException("Inventory not found for product");
            if (inventory.Quantity < item.Quantity)
                return Conflict(new { error = $"Produk {product.Name} tidak cukup stok" });
            total += product.Price * item.Quantity;
        }

        // Generate order number (simple format)
        var orderNumber = $"ORD-{DateTime.UtcNow:yyyyMMddHHmmss}-{new Random().Next(100, 999)}";

        var order = new Order
        {
            Id = Guid.NewGuid(),
            SchoolId = schoolId,
            UserId = userId,
            OrderNumber = orderNumber,
            TotalAmount = total,
            Status = "Menunggu Konfirmasi",
            PickupTime = req.PickupTime,
            CreatedAt = DateTime.UtcNow,
            UpdatedAt = DateTime.UtcNow,
            Items = req.Items.Select(i => new OrderItem
            {
                Id = Guid.NewGuid(),
                ProductId = i.ProductId,
                Quantity = i.Quantity,
                UnitPrice = products.First(p => p.Id == i.ProductId).Price
            }).ToList()
        };

        // Reserve stock using optimistic concurrency (RowVersion)
        foreach (var item in req.Items)
        {
            var inventory = products.First(p => p.Id == item.ProductId).Inventories.First(i => i.SchoolId == schoolId);
            inventory.Quantity -= item.Quantity;
            _db.Entry(inventory).OriginalValues["RowVersion"] = inventory.RowVersion; // use current version for concurrency check
        }

        // Create payment placeholder
        var payment = new Payment
        {
            Id = Guid.NewGuid(),
            OrderId = order.Id,
            Method = req.PaymentMethod,
            Amount = total,
            Status = req.PaymentMethod.Equals("Cash", StringComparison.OrdinalIgnoreCase) ? "Completed" : "Pending",
            CreatedAt = DateTime.UtcNow
        };
        order.Payment = payment;

        _db.Orders.Add(order);
        _db.Payments.Add(payment);
        await _db.SaveChangesAsync();

        // In real implementation you would call Midtrans API for QR‑IS here.
        return Ok(new CreateOrderResponse(orderNumber, order.Status));
    }

    // GET api/v1/orders (list current user's orders)
    [HttpGet]
    public async Task<ActionResult<List<Order>>> GetOrders([FromQuery] int page = 1, [FromQuery] int size = 20)
    {
        var userId = Guid.Parse(User.FindFirst("sub")?.Value ?? throw new InvalidOperationException("Missing sub claim"));
        var query = _db.Orders
            .Where(o => o.UserId == userId)
            .OrderByDescending(o => o.CreatedAt)
            .Skip((page - 1) * size)
            .Take(size);
        var list = await query.ToListAsync();
        return Ok(list);
    }

    // GET api/v1/orders/{id}
    [HttpGet("{id:guid}")]
    public async Task<ActionResult<Order>> GetOrder(Guid id)
    {
        var userId = Guid.Parse(User.FindFirst("sub")?.Value ?? throw new InvalidOperationException("Missing sub claim"));
        var order = await _db.Orders
            .Include(o => o.Items)
                .ThenInclude(i => i.Product)
            .Include(o => o.Payment)
            .FirstOrDefaultAsync(o => o.Id == id && o.UserId == userId);
        if (order == null) return NotFound();
        return Ok(order);
    }
}

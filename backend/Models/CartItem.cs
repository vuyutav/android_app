using System;
using System.Collections.Generic;

namespace SchoolMinimarket.Backend.Models;

public class CartItem
{
    public Guid Id { get; set; }
    public Guid CartId { get; set; }
    public Guid ProductId { get; set; }
    public int Quantity { get; set; }
    public DateTime AddedAt { get; set; } = DateTime.UtcNow;

    // Navigation
    public Cart? Cart { get; set; }
    public Product? Product { get; set; }
}

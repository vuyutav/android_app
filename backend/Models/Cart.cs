using System.Collections.Generic;
using System;

namespace SchoolMinimarket.Backend.Models;

public class Cart
{
    public Guid Id { get; set; }
    public Guid UserId { get; set; }
    public Guid SchoolId { get; set; }
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;

    public ICollection<CartItem> Items { get; set; } = new List<CartItem>();
}

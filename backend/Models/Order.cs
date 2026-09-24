using System;
using System.Collections.Generic;

namespace SchoolMinimarket.Backend.Models;

public class Order
{
    public Guid Id { get; set; }
    public Guid SchoolId { get; set; }
    public Guid UserId { get; set; }
    public string OrderNumber { get; set; } = null!; // e.g., "ORD-20230925-001"
    public decimal TotalAmount { get; set; }
    public string Status { get; set; } = "Menunggu Konfirmasi"; // default status
    public DateTime? PickupTime { get; set; }
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;

    // Navigation
    public School? School { get; set; }
    public User? User { get; set; }
    public ICollection<OrderItem> Items { get; set; } = new List<OrderItem>();
    public Payment? Payment { get; set; }
}

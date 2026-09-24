using System;

namespace SchoolMinimarket.Backend.Models;

public class Payment
{
    public Guid Id { get; set; }
    public Guid OrderId { get; set; }
    public string Method { get; set; } = null!; // Cash, QRIS, etc.
    public decimal Amount { get; set; }
    public string Status { get; set; } = "Pending"; // Pending, Completed, Failed
    public string? TransactionId { get; set; }
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public DateTime? UpdatedAt { get; set; }

    // Navigation
    public Order? Order { get; set; }
}

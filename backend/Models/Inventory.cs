using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace SchoolMinimarket.Backend.Models;

public class Inventory
{
    public Guid Id { get; set; }
    public Guid SchoolId { get; set; }
    public Guid ProductId { get; set; }
    public int Quantity { get; set; }
    public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;

    // Concurrency token – EF Core will use this for optimistic concurrency.
    [Timestamp]
    public byte[] RowVersion { get; set; } = null!;

    // Navigation
    public School? School { get; set; }
    public Product? Product { get; set; }
}

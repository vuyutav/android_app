using System;
using System.Collections.Generic;

namespace SchoolMinimarket.Backend.Models;

public class School
{
    public Guid Id { get; set; }
    public string Code { get; set; } = null!; // e.g., "S001"
    public string Name { get; set; } = null!;
    public string TimeZone { get; set; } = "Asia/Jakarta";
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;

    // Navigation collections
    public ICollection<User> Users { get; set; } = new List<User>();
    public ICollection<ProductCategory> Categories { get; set; } = new List<ProductCategory>();
    public ICollection<Product> Products { get; set; } = new List<Product>();
    public ICollection<Inventory> Inventories { get; set; } = new List<Inventory>();
    public ICollection<Order> Orders { get; set; } = new List<Order>();
}

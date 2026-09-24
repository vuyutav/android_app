using System.Collections.Generic;

namespace SchoolMinimarket.Backend.Models;

public class ProductCategory
{
    public Guid Id { get; set; }
    public Guid SchoolId { get; set; }
    public string Name { get; set; } = null!;
    public string? Description { get; set; }
    public int SortOrder { get; set; } = 0;
    public bool IsDeleted { get; set; } = false;
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;

    // Navigation
    public School? School { get; set; }
    public ICollection<Product> Products { get; set; } = new List<Product>();
}

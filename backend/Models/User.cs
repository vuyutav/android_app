using System;
using System.Collections.Generic;

namespace SchoolMinimarket.Backend.Models;

public class User
{
    public Guid Id { get; set; }
    public Guid SchoolId { get; set; }
    public Guid RoleId { get; set; }
    public string Email { get; set; } = null!;
    public string PasswordHash { get; set; } = null!;
    public string FullName { get; set; } = null!;
    public string? NISN { get; set; } // Student identifier (optional for staff)
    public bool IsActive { get; set; } = true;
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;

    // Navigation
    public School? School { get; set; }
    public Role? Role { get; set; }
    public ICollection<Order> Orders { get; set; } = new List<Order>();
}

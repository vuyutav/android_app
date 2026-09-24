using System.Collections.Generic;

namespace SchoolMinimarket.Backend.Models;

public class Role
{
    public Guid Id { get; set; }
    public string Name { get; set; } = null!; // Student, Teacher, Staff, Cashier, Admin
    public string Description { get; set; } = string.Empty;
    public ICollection<User> Users { get; set; } = new List<User>();
}

using Microsoft.EntityFrameworkCore;
using SchoolMinimarket.Backend.Models;

namespace SchoolMinimarket.Backend;

public class SchoolMinimarketContext : DbContext
{
    public SchoolMinimarketContext(DbContextOptions<SchoolMinimarketContext> options)
        : base(options)
    { }

    public DbSet<School> Schools => Set<School>();
    public DbSet<User> Users => Set<User>();
    public DbSet<Role> Roles => Set<Role>();
    public DbSet<ProductCategory> ProductCategories => Set<ProductCategory>();
    public DbSet<Product> Products => Set<Product>();
    public DbSet<Inventory> Inventories => Set<Inventory>();
    public DbSet<Order> Orders => Set<Order>();
    public DbSet<OrderItem> OrderItems => Set<OrderItem>();
    public DbSet<Payment> Payments => Set<Payment>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        // Global soft‑delete filter (if needed later)
        // modelBuilder.Entity<...>().HasQueryFilter(e => !e.IsDeleted);

        // Unique constraints
        modelBuilder.Entity<User>()
            .HasIndex(u => u.Email)
            .IsUnique();
        modelBuilder.Entity<User>()
            .HasIndex(u => u.NISN)
            .IsUnique(false);
        modelBuilder.Entity<ProductCategory>()
            .HasIndex(c => new { c.SchoolId, c.Name })
            .IsUnique();
        modelBuilder.Entity<Product>()
            .HasIndex(p => new { p.SchoolId, p.Name })
            .IsUnique();
        modelBuilder.Entity<Order>()
            .HasIndex(o => o.OrderNumber)
            .IsUnique();
        modelBuilder.Entity<Inventory>()
            .HasIndex(i => new { i.SchoolId, i.ProductId })
            .IsUnique();

        // Relationships
        modelBuilder.Entity<User>()
            .HasOne(u => u.School)
            .WithMany(s => s.Users)
            .HasForeignKey(u => u.SchoolId)
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<User>()
            .HasOne(u => u.Role)
            .WithMany(r => r.Users)
            .HasForeignKey(u => u.RoleId);

        modelBuilder.Entity<ProductCategory>()
            .HasOne(c => c.School)
            .WithMany(s => s.Categories)
            .HasForeignKey(c => c.SchoolId)
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<Product>()
            .HasOne(p => p.School)
            .WithMany(s => s.Products)
            .HasForeignKey(p => p.SchoolId)
            .OnDelete(DeleteBehavior.Cascade);
        modelBuilder.Entity<Product>()
            .HasOne(p => p.Category)
            .WithMany(c => c.Products)
            .HasForeignKey(p => p.CategoryId);

        modelBuilder.Entity<Inventory>()
            .HasOne(i => i.Product)
            .WithMany(p => p.Inventories)
            .HasForeignKey(i => i.ProductId);
        modelBuilder.Entity<Inventory>()
            .HasOne(i => i.School)
            .WithMany(s => s.Inventories)
            .HasForeignKey(i => i.SchoolId);

        modelBuilder.Entity<Order>()
            .HasOne(o => o.User)
            .WithMany(u => u.Orders)
            .HasForeignKey(o => o.UserId);
        modelBuilder.Entity<Order>()
            .HasOne(o => o.School)
            .WithMany(s => s.Orders)
            .HasForeignKey(o => o.SchoolId);

        modelBuilder.Entity<OrderItem>()
            .HasOne(oi => oi.Order)
            .WithMany(o => o.Items)
            .HasForeignKey(oi => oi.OrderId);
        modelBuilder.Entity<OrderItem>()
            .HasOne(oi => oi.Product)
            .WithMany(p => p.OrderItems)
            .HasForeignKey(oi => oi.ProductId);

        modelBuilder.Entity<Payment>()
            .HasOne(p => p.Order)
            .WithOne(o => o.Payment)
            .HasForeignKey<Payment>(p => p.OrderId);

        // Concurrency token for inventory updates
        modelBuilder.Entity<Inventory>()
            .Property(i => i.RowVersion)
            .IsRowVersion();
    }
}

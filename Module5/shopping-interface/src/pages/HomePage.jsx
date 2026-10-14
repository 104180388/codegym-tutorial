import React, { useState, useEffect } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import { Sparkles, ArrowRight, ShoppingCart, Truck, ShieldCheck, RotateCcw, Headphones } from 'lucide-react';
import { productService } from '../api/productService';
import CategoryList from '../components/product/CategoryList';
import ProductFilter from '../components/product/ProductFilter';
import ProductList from '../components/product/ProductList';
import { formatCurrency } from '../utils/formatters';

export default function HomePage() {
  const [searchParams] = useSearchParams();

  // Local state demonstrating React hooks
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState(searchParams.get('q') || '');
  const [selectedBrand, setSelectedBrand] = useState('Tất cả');
  const [sortBy, setSortBy] = useState('default');
  const [priceFilter, setPriceFilter] = useState('all');

  // Sync searchTerm if URL search query changes
  useEffect(() => {
    const q = searchParams.get('q');
    if (q !== null) {
      setSearchTerm(q);
    }
  }, [searchParams]);

  // Fetch catalog from mock API via Axios on mount
  useEffect(() => {
    let isMounted = true;
    const fetchCatalog = async () => {
      try {
        setLoading(true);
        const data = await productService.getAllProducts();
        if (isMounted) {
          setProducts(data);
        }
      } catch (err) {
        console.error('Error fetching catalog:', err);
      } finally {
        if (isMounted) setLoading(false);
      }
    };

    fetchCatalog();
    return () => {
      isMounted = false;
    };
  }, []);

  // Filter & Search Logic
  const filteredProducts = products.filter((item) => {
    const name = item['product-name'] || item.name || '';
    const desc = item['product-description'] || item.description || '';
    const price = item.price || 0;

    const matchesSearch =
      name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      desc.toLowerCase().includes(searchTerm.toLowerCase());

    const matchesBrand =
      selectedBrand === 'Tất cả' ||
      name.toLowerCase().includes(selectedBrand.toLowerCase());

    let matchesPrice = true;
    if (priceFilter === 'under-35') matchesPrice = price < 35000000;
    else if (priceFilter === '35-45') matchesPrice = price >= 35000000 && price <= 45000000;
    else if (priceFilter === 'over-45') matchesPrice = price > 45000000;

    return matchesSearch && matchesBrand && matchesPrice;
  });

  // Sort Logic
  const sortedProducts = [...filteredProducts].sort((a, b) => {
    const nameA = a['product-name'] || a.name || '';
    const nameB = b['product-name'] || b.name || '';
    if (sortBy === 'price-asc') return a.price - b.price;
    if (sortBy === 'price-desc') return b.price - a.price;
    if (sortBy === 'name-asc') return nameA.localeCompare(nameB);
    return 0;
  });

  const handleResetFilters = () => {
    setSearchTerm('');
    setSelectedBrand('Tất cả');
    setSortBy('default');
    setPriceFilter('all');
  };

  const featuredLaptop = products[0] || null;

  return (
    <div className="home-page">
      {/* ================= HERO SECTION ================= */}
      <section className="hero-section">
        <div className="container">
          <div className="hero-grid">
            <div className="hero-content">
              <div className="hero-tag">
                <Sparkles size={16} color="#818cf8" />
                <span>Thế Giới Laptop Hi-End 2026</span>
              </div>
              <h1 className="hero-title">
                Khám Phá Sức Mạnh <br />
                <span className="gradient-text">Laptop Thế Hệ Mới</span>
              </h1>
              <p className="hero-description">
                Trải nghiệm những mẫu laptop gaming đỉnh cao, ultrabook doanh nhân siêu mỏng nhẹ trang bị chip AI thế hệ mới. Cam kết 100% hàng chính hãng với ưu đãi đặc quyền.
              </p>

              <div className="hero-actions">
                <button
                  type="button"
                  className="btn btn-primary btn-lg"
                  onClick={() => {
                    window.scrollBy({ top: 560, behavior: 'smooth' });
                  }}
                >
                  <span>Khám phá ngay</span>
                  <ArrowRight size={18} />
                </button>
                <Link to="/cart" className="btn btn-secondary btn-lg">
                  <ShoppingCart size={18} />
                  <span>Xem giỏ hàng</span>
                </Link>
              </div>

              {/* Quick stats */}
              <div className="hero-stats">
                <div className="stat-item">
                  <h4>100%</h4>
                  <p>Chính Hãng Phân Phối</p>
                </div>
                <div className="stat-item">
                  <h4>24 Tháng</h4>
                  <p>Bảo Hành Toàn Diện</p>
                </div>
                <div className="stat-item">
                  <h4>2 Giờ</h4>
                  <p>Giao Siêu Tốc Nội Thành</p>
                </div>
              </div>
            </div>

            {/* Hero Visual Card */}
            {featuredLaptop && (
              <div className="hero-preview">
                <div className="hero-card">
                  <div style={{ position: 'relative' }}>
                    <img
                      src={featuredLaptop['product-picture']}
                      alt={featuredLaptop['product-name']}
                    />
                    <div style={{ position: 'absolute', top: 12, right: 12 }}>
                      <span className="badge badge-brand">Flagship Đỉnh Cao</span>
                    </div>
                  </div>
                  <div className="hero-card-meta">
                    <div>
                      <h3 className="hero-card-title">{featuredLaptop['product-name']}</h3>
                      <p style={{ color: 'var(--text-muted)', fontSize: '0.82rem', marginTop: 4 }}>
                        Chip Apple M3 Max • RAM 36GB • 120Hz ProMotion
                      </p>
                    </div>
                    <div className="hero-card-price">{formatCurrency(featuredLaptop.price)}</div>
                  </div>
                  <div style={{ marginTop: '1.25rem', display: 'flex', gap: '0.75rem' }}>
                    <Link
                      to={`/product/${featuredLaptop.id}`}
                      className="btn btn-primary btn-block btn-sm"
                    >
                      Xem chi tiết sản phẩm
                    </Link>
                  </div>
                </div>
              </div>
            )}
          </div>

          {/* ================= FEATURES BAR ================= */}
          <div className="features-bar">
            <div className="feature-box">
              <div className="feature-icon-wrapper">
                <Truck size={22} />
              </div>
              <div>
                <h5>Miễn Phí Vận Chuyển</h5>
                <p>Giao hàng toàn quốc an toàn</p>
              </div>
            </div>

            <div className="feature-box">
              <div className="feature-icon-wrapper">
                <ShieldCheck size={22} />
              </div>
              <div>
                <h5>Bảo Hành 24 Tháng</h5>
                <p>Chính hãng 1 đổi 1 trong 30 ngày</p>
              </div>
            </div>

            <div className="feature-box">
              <div className="feature-icon-wrapper">
                <RotateCcw size={22} />
              </div>
              <div>
                <h5>Hỗ Trợ Trả Góp 0%</h5>
                <p>Thủ tục đơn giản qua thẻ</p>
              </div>
            </div>

            <div className="feature-box">
              <div className="feature-icon-wrapper">
                <Headphones size={22} />
              </div>
              <div>
                <h5>Tư Vấn Kỹ Thuật 24/7</h5>
                <p>Đội ngũ chuyên gia tận tâm</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* ================= PRODUCTS CATALOG SECTION ================= */}
      <section className="catalog-section">
        <div className="container">
          <div className="section-header">
            <div>
              <h2 className="section-title">Sản Phẩm Laptop Nổi Bật</h2>
              <p className="section-subtitle">
                Tìm kiếm sản phẩm phù hợp cho nhu cầu làm việc, học tập và giải trí của bạn
              </p>
            </div>

            {/* ProductFilter Component (Search, Sort, Price) */}
            <ProductFilter
              searchTerm={searchTerm}
              onSearchChange={setSearchTerm}
              sortBy={sortBy}
              onSortChange={setSortBy}
              priceFilter={priceFilter}
              onPriceFilterChange={setPriceFilter}
            />
          </div>

          {/* CategoryList Component (Brand tabs) */}
          <CategoryList
            selectedCategory={selectedBrand}
            onSelectCategory={setSelectedBrand}
          />

          {/* ProductList Component (Grid of ProductCards) */}
          <ProductList
            products={sortedProducts}
            loading={loading}
            emptyMessage="Không tìm thấy mẫu laptop phù hợp"
            onResetFilters={handleResetFilters}
          />
        </div>
      </section>
    </div>
  );
}

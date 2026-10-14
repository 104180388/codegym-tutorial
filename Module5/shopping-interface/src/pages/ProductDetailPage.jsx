import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { Sparkles, ArrowLeft } from 'lucide-react';
import { productService } from '../api/productService';
import ProductDetail from '../components/product/ProductDetail';
import ProductList from '../components/product/ProductList';

export default function ProductDetailPage() {
  const { id } = useParams();

  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [relatedProducts, setRelatedProducts] = useState([]);

  // Fetch product detail using Axios via productService
  useEffect(() => {
    let isMounted = true;
    const loadDetail = async () => {
      try {
        setLoading(true);
        window.scrollTo(0, 0);

        const data = await productService.getProductById(id);
        if (isMounted) {
          setProduct(data);
        }

        // Fetch related products
        const all = await productService.getAllProducts();
        if (isMounted) {
          setRelatedProducts(all.filter((p) => String(p.id) !== String(id)).slice(0, 4));
        }
      } catch (err) {
        console.error('Lỗi khi tải chi tiết laptop:', err);
      } finally {
        if (isMounted) setLoading(false);
      }
    };

    loadDetail();
    return () => {
      isMounted = false;
    };
  }, [id]);

  if (loading) {
    return (
      <div className="container" style={{ padding: '6rem 0', textAlign: 'center' }}>
        <div
          className="modal-success-icon"
          style={{ borderColor: 'var(--primary)', color: 'var(--primary)', margin: '0 auto 1.5rem auto' }}
        >
          <Sparkles size={32} />
        </div>
        <h2 style={{ color: '#ffffff' }}>Đang tải thông tin sản phẩm...</h2>
      </div>
    );
  }

  if (!product) {
    return (
      <div className="container" style={{ padding: '6rem 0', textAlign: 'center' }}>
        <h2 style={{ color: '#ffffff' }}>Không tìm thấy sản phẩm</h2>
        <p style={{ color: 'var(--text-muted)', margin: '1rem 0 2rem 0' }}>
          Sản phẩm bạn đang tìm kiếm có thể đã hết hàng hoặc không tồn tại.
        </p>
        <Link to="/" className="btn btn-primary">
          <ArrowLeft size={18} /> Về trang chủ
        </Link>
      </div>
    );
  }

  const name = product['product-name'] || product.name;

  return (
    <div className="detail-page">
      <div className="container">
        {/* Breadcrumbs */}
        <nav className="breadcrumbs" aria-label="Đường dẫn điều hướng">
          <Link to="/">Trang chủ</Link>
          <span>/</span>
          <span className="current">{name}</span>
        </nav>

        {/* ProductDetail Component */}
        <ProductDetail product={product} />

        {/* Related Products Section using ProductList Component */}
        {relatedProducts.length > 0 && (
          <div style={{ marginTop: '4.5rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
              <h3 style={{ fontSize: '1.6rem', fontWeight: 800 }}>Sản Phẩm Tương Tự</h3>
              <Link to="/" style={{ color: '#818cf8', fontWeight: 600, fontSize: '0.92rem' }}>
                Xem tất cả →
              </Link>
            </div>

            <ProductList products={relatedProducts} />
          </div>
        )}
      </div>
    </div>
  );
}

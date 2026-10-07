export default function Blog() {
    return (
        <div style={styles.container}>
            <h1 style={styles.title}>📰 Blog</h1>
            <p style={styles.text}>
                Explore our latest blog posts and updates about web development, design trends, and product news.
            </p>
        </div>
    );
}

const styles = {
    container: {
        background: '#fff',
        padding: '40px',
        borderRadius: '12px',
        boxShadow: '0 10px 30px rgba(0,0,0,0.1)',
        textAlign: 'center',
    },
    title: {
        fontSize: '32px',
        marginBottom: '20px',
        color: '#16a085',
    },
    text: {
        fontSize: '18px',
        color: '#555',
    },
};
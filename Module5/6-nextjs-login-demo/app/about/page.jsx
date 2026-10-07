export default function About() {
    return (
        <div style={styles.container}>
            <h1 style={styles.title}>📘 About Us</h1>
            <p style={styles.text}>
                We are a small team dedicated to building fast, accessible, and beautiful web experiences with Next.js.
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
        color: '#2980b9',
    },
    text: {
        fontSize: '18px',
        color: '#555',
    },
};
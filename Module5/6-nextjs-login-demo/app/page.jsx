export default function Home() {
    return (
        <div style={styles.container}>
            <h1 style={styles.title}>🏠 Welcome to the Home Page</h1>
            <p style={styles.text}>
                This is the main landing page of our website. You can navigate to About Us or Blog from the menu above.
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
        color: '#2c3e50',
    },
    text: {
        fontSize: '18px',
        color: '#555',
    },
};
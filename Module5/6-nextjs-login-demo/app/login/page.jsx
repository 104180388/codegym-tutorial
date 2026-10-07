'use client'
import { useRouter } from 'next/navigation'
import styles from './Login.module.css'

export default function Login() {
    const router = useRouter();

    const handleLogin = () => {
        router.push('/');
    };

    return (
        <div className={styles.wrapper}>
            <div className={styles.formContent}>
                <h2>Login</h2>
                <input className={styles.input} type="text" placeholder="Username" />
                <input className={styles.input} type="password" placeholder="Password" />
                <button className={styles.button} onClick={handleLogin}>Log In</button>
            </div>
        </div>
    );
}
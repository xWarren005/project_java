const express = require("express");
const cors = require("cors");
const mysql = require("mysql2/promise");
require("dotenv").config();

const app = express();
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

const pool = mysql.createPool({
  host: process.env.DB_HOST,
  port: Number(process.env.DB_PORT),
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  database: process.env.DB_NAME,
});

function mapUserRow(row) {
  if (!row) return null;
  return {
    id: row.id,
    fullName: row.full_name,
    username: row.username,
    email: row.email,
    phone: row.phone,
    avatarUrl: row.avatar_url,
    role: row.role,
  };
}

async function ensureDevUser() {
  const username = "thinh123";
  const password = "123456";
  try {
    const [rows] = await pool.query(
      "SELECT id FROM users WHERE username = ? LIMIT 1",
      [username]
    );
    if (rows && rows.length > 0) return;

    await pool.query(
      "INSERT INTO users (username, password_hash, full_name, role, created_at) VALUES (?, ?, ?, 'CUSTOMER', NOW())",
      [username, password, "Thinh User"]
    );
    console.log("Seeded dev user:", username);
  } catch (e) {
    console.error("Seed dev user failed:", e.message);
  }
}

app.get("/api/health", async (req, res) => {
  try {
    const [rows] = await pool.query("SELECT 1 AS ok");
    res.json(rows[0]);
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

app.get("/api/restaurants", async (req, res) => {
  try {
    const [rows] = await pool.query(
      "SELECT id, name, address, description, " +
        "cover_image AS imageUrl, avg_rating AS avgRating, " +
        "0 AS reviewCount, 0 AS favorite " +
        "FROM restaurants"
    );
    res.json(rows.map(r => ({ ...r, favorite: false })));
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

app.get("/restaurant", async (req, res) => {
  try {
    const limit = req.query.size ? parseInt(req.query.size, 10) : null;
    const sql =
      "SELECT id, name, address, description, " +
      "cover_image AS imageUrl, avg_rating AS avgRating, " +
      "0 AS reviewCount, 0 AS favorite " +
      "FROM restaurants " +
      (limit && limit > 0 ? "LIMIT ?" : "");
    const [rows] = limit && limit > 0 ? await pool.query(sql, [limit]) : await pool.query(sql);
    res.json(rows.map(r => ({ ...r, favorite: false })));
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

app.get("/restaurants/featured", async (req, res) => {
  try {
    const limit = req.query.limit ? parseInt(req.query.limit, 10) : 10;
    const [rows] = await pool.query(
      "SELECT id, name, address, description, " +
        "cover_image AS imageUrl, avg_rating AS avgRating, " +
        "0 AS reviewCount, 0 AS favorite " +
        "FROM restaurants " +
        "ORDER BY avg_rating DESC, id DESC " +
        "LIMIT ?",
      [limit]
    );
    res.json(rows.map(r => ({ ...r, favorite: false })));
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

app.get("/restaurants/recommended", async (req, res) => {
  try {
    const limit = req.query.limit ? parseInt(req.query.limit, 10) : 10;
    const [rows] = await pool.query(
      "SELECT id, name, address, description, " +
        "cover_image AS imageUrl, avg_rating AS avgRating, " +
        "0 AS reviewCount, 0 AS favorite " +
        "FROM restaurants " +
        "ORDER BY id DESC " +
        "LIMIT ?",
      [limit]
    );
    res.json(rows.map(r => ({ ...r, favorite: false })));
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

app.get("/restaurants/search", async (req, res) => {
  try {
    const keyword = (req.query.q || "").trim();
    const limit = req.query.size ? parseInt(req.query.size, 10) : 20;
    if (!keyword) {
      return res.json([]);
    }
    const [rows] = await pool.query(
      "SELECT id, name, address, description, " +
        "cover_image AS imageUrl, avg_rating AS avgRating, " +
        "0 AS reviewCount, 0 AS favorite " +
        "FROM restaurants " +
        "WHERE name LIKE ? OR address LIKE ? " +
        "ORDER BY avg_rating DESC, id DESC " +
        "LIMIT ?",
      [`%${keyword}%`, `%${keyword}%`, limit]
    );
    res.json(rows.map(r => ({ ...r, favorite: false })));
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

app.get("/restaurants/:id", async (req, res) => {
  try {
    const id = parseInt(req.params.id, 10);
    if (!id) return res.status(400).json({ error: "Invalid id" });
    const [rows] = await pool.query(
      "SELECT id, name, address, description, " +
        "cover_image AS imageUrl, avg_rating AS avgRating, " +
        "0 AS reviewCount, 0 AS favorite " +
        "FROM restaurants WHERE id = ?",
      [id]
    );
    if (!rows || rows.length === 0) return res.status(404).json({ error: "Not found" });
    res.json({ ...rows[0], favorite: false });
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

app.post("/api/mobile/auth/register", async (req, res) => {
  try {
    const username = (req.body.username || "").trim();
    const password = req.body.password || "";
    const fullName = (req.body.fullName || "").trim() || null;
    const email = (req.body.email || "").trim() || null;
    const phone = (req.body.phone || "").trim() || null;

    if (!username || !password) {
      return res.status(400).json({ error: "Missing username or password" });
    }

    const [exists] = await pool.query(
      "SELECT id FROM users WHERE username = ? LIMIT 1",
      [username]
    );
    if (exists && exists.length > 0) {
      return res.status(409).json({ error: "Username already exists" });
    }

    const [result] = await pool.query(
      "INSERT INTO users (username, password_hash, full_name, email, phone, role, created_at) VALUES (?, ?, ?, ?, ?, 'CUSTOMER', NOW())",
      [username, password, fullName, email, phone]
    );

    const [rows] = await pool.query(
      "SELECT id, full_name, username, email, phone, avatar_url, role FROM users WHERE id = ?",
      [result.insertId]
    );
    res.json(mapUserRow(rows[0]));
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

app.post("/api/mobile/auth/login", async (req, res) => {
  try {
    const login = (req.body.username || "").trim();
    const password = req.body.password || "";
    if (!login || !password) {
      return res.status(400).json({ error: "Missing credentials" });
    }

    const [rows] = await pool.query(
      "SELECT id, full_name, username, email, phone, avatar_url, role, password_hash FROM users WHERE username = ? OR email = ? LIMIT 1",
      [login, login]
    );
    if (!rows || rows.length === 0) {
      return res.status(401).json({ error: "Invalid username or password" });
    }

    const row = rows[0];
    if ((row.password_hash || "") !== password) {
      return res.status(401).json({ error: "Invalid username or password" });
    }

    res.json({
      token: `dev-token-${row.id}`,
      user: mapUserRow(row),
    });
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

app.listen(process.env.PORT, async () => {
  await ensureDevUser();
  console.log("API running on port", process.env.PORT);
});

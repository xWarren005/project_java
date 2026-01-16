const express = require("express");
const cors = require("cors");
const mysql = require("mysql2/promise");
require("dotenv").config();

const app = express();
app.use(cors());
app.use(express.json());

const pool = mysql.createPool({
  host: process.env.DB_HOST,
  port: Number(process.env.DB_PORT),
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  database: process.env.DB_NAME,
});

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

app.listen(process.env.PORT, () =>
  console.log("API running on port", process.env.PORT)
);

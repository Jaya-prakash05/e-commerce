# E-Commerce Application

A Spring Boot + Maven + MySQL e-commerce backend with product catalog, shopping cart, and order management.

## Features

- Product catalogue management
- Add/update/remove cart items
- Checkout and save orders
- MySQL persistence
- RESTful API endpoints

## Suggested ports

- Application: 8081
- MySQL: 3306

Because Jenkins is already running on port 8080, this app uses 8081 to avoid conflicts.

## Run locally

This project runs by default with an in-memory H2 database so it starts without MySQL.

```bash
mvn spring-boot:run
```

The app will be available at http://localhost:8081

## Optional MySQL profile

If you want to use MySQL instead, create a database named `ecommerce_db`, then run:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

This uses the configuration in `src/main/resources/application-mysql.properties`.

## Docker

This project is ready for Docker. The app is mapped to port `8081` so it does not conflict with Jenkins on `8080`, and MySQL uses `3306`.

```bash
docker compose up --build
```

Then access:

- App: http://localhost:8081
- MySQL: localhost:3306

## API overview

- `GET /api/products`
- `GET /api/products/{id}`
- `POST /api/products`
- `PUT /api/products/{id}`
- `DELETE /api/products/{id}`
- `GET /api/cart`
- `POST /api/cart/items`
- `PUT /api/cart/items/{productId}`
- `DELETE /api/cart/items/{productId}`
- `POST /api/orders`
- `GET /api/orders`
- `GET /api/orders/{id}`

## Example product payload

```json
{
  "name": "Wireless Mouse",
  "description": "Ergonomic wireless mouse for office use.",
  "price": 29.99,
  "stock": 120,
  "imageUrl": "https://example.com/mouse.jpg"
}
```

## Example cart payload

```json
{
  "productId": 1,
  "quantity": 2
}
```

## Example order payload

```json
{
  "customerName": "Jane Doe",
  "email": "jane@example.com"
}
```

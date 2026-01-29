URL Shortener Service

Built a Bitly-inspired URL shortener using Spring Boot, MySQL, and Docker, 
with support for custom aliases, link expiration, and click analytics.

🔑 Key Highlights (Resume-Focused)

• Designed and implemented a RESTful URL Shortener backend using Spring Boot (Java 17)
• Implemented custom short aliases, URL expiration, and redirect handling
• Built click tracking & analytics for monitoring URL usage
• Integrated MySQL 8.0 with optimized schema design
• Containerized application using Docker & Docker Compose
• Followed clean architecture, layered design, and best REST practices
• Production-ready configuration with environment-based profiles

🧠 System Design & Architecture

• Layered Architecture
• Controller → Service → Repository
• Database Design
• Indexed short codes for fast lookups
• Expiration-based URL validation
• Scalability Considerations
• Stateless REST APIs
• Easily extensible for caching (Redis) and load balancing
• Error Handling
• Centralized exception handling with meaningful API responses

🛠️ Tech Stack
• Category	Technologies
• Language	Java 17
• Framework	Spring Boot
• Database	MySQL 8.0
• Build Tool	Maven
• Containers	Docker, Docker Compose
• API	RESTful Web Services
• 📡 Core API Endpoints
• Method	Endpoint	Description
• POST	/api/shorten	Generate short URL
• GET	/{shortCode}	Redirect to original URL
• GET	/api/analytics/{shortCode}	Retrieve click statistics

⚙️ Deployment & DevOps

• Dockerized application for consistent deployments
• Multi-container setup with Spring Boot + MySQL
• Supports both local and containerized environments
• Easily deployable to AWS / GCP / Azure / VPS

📈 Impact

• Reduced long URLs into short, shareable links
• Enabled analytics for tracking user engagement
• Designed to scale with minimal configuration changes

🚀 Future Enhancements

• Redis caching for high-traffic URLs
• Authentication & role-based access
• Rate limiting & abuse prevention
• QR code generation
• Admin analytics dashboard

👤 Author

Azhar Mansoori
Backend Developer | Java | Spring Boot
GitHub: https://github.com/azhardev77
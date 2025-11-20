-- ============================================================================
-- Database Schema for NTT-MVC Authentication System
-- ============================================================================
-- Description: Complete database schema for user authentication system
-- Database: PostgreSQL / MySQL / H2 compatible
-- Version: 1.0
-- ============================================================================

-- ============================================================================
-- DROP TABLES (if exists) - For clean reinstall
-- ============================================================================

DROP TABLE IF EXISTS authentication_audit CASCADE;
DROP TABLE IF EXISTS refresh_tokens CASCADE;
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS phone CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS roles CASCADE;

-- ============================================================================
-- TABLE: roles
-- Description: Stores user roles for authorization
-- ============================================================================

CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL UNIQUE,
    
    -- Audit fields
    created_by VARCHAR(255),
    created_date TIMESTAMP,
    last_modified_by VARCHAR(255),
    last_modified_date TIMESTAMP,
    
    -- Soft delete
    is_deleted BOOLEAN DEFAULT FALSE
);

-- ============================================================================
-- TABLE: users
-- Description: Stores user account information
-- ============================================================================

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    lastlogin TIMESTAMP,
    
    -- Audit fields
    created_by VARCHAR(255),
    created_date TIMESTAMP,
    last_modified_by VARCHAR(255),
    last_modified_date TIMESTAMP,
    
    -- Soft delete
    is_deleted BOOLEAN DEFAULT FALSE
);

-- ============================================================================
-- TABLE: user_roles
-- Description: Many-to-many relationship between users and roles
-- ============================================================================

CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- ============================================================================
-- TABLE: phone
-- Description: Stores phone numbers associated with users
-- ============================================================================

CREATE TABLE phone (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID,
    number VARCHAR(14),
    city_code VARCHAR(4),
    country_code VARCHAR(4),
    
    -- Audit fields
    created_by VARCHAR(255),
    created_date TIMESTAMP,
    last_modified_by VARCHAR(255),
    last_modified_date TIMESTAMP,
    
    -- Soft delete
    is_deleted BOOLEAN DEFAULT FALSE,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================================================
-- TABLE: refresh_tokens
-- Description: Stores refresh tokens for JWT authentication
-- ============================================================================

CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    user_id UUID NOT NULL,
    
    -- Audit fields
    created_by VARCHAR(255),
    created_date TIMESTAMP,
    last_modified_by VARCHAR(255),
    last_modified_date TIMESTAMP,
    
    -- Soft delete
    is_deleted BOOLEAN DEFAULT FALSE,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================================================
-- TABLE: authentication_audit
-- Description: Audit trail for authentication events
-- ============================================================================

CREATE TABLE authentication_audit (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    event_type VARCHAR(20) NOT NULL,
    access_token_hash VARCHAR(64),
    refresh_token_id UUID,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    event_time TIMESTAMP NOT NULL,
    successful BOOLEAN NOT NULL,
    
    -- Audit fields
    created_by VARCHAR(255),
    created_date TIMESTAMP,
    last_modified_by VARCHAR(255),
    last_modified_date TIMESTAMP,
    
    -- Soft delete
    is_deleted BOOLEAN DEFAULT FALSE,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================================================
-- INDEXES for Performance Optimization
-- ============================================================================

-- Users indexes
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_is_deleted ON users(is_deleted);

-- Roles indexes
CREATE INDEX idx_roles_name ON roles(name);
CREATE INDEX idx_roles_is_deleted ON roles(is_deleted);

-- Phone indexes
CREATE INDEX idx_phone_user_id ON phone(user_id);
CREATE INDEX idx_phone_is_deleted ON phone(is_deleted);

-- Refresh tokens indexes
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_expiry_date ON refresh_tokens(expiry_date);
CREATE INDEX idx_refresh_tokens_is_deleted ON refresh_tokens(is_deleted);

-- Authentication audit indexes
CREATE INDEX idx_auth_audit_user_id ON authentication_audit(user_id);
CREATE INDEX idx_auth_audit_event_type ON authentication_audit(event_type);
CREATE INDEX idx_auth_audit_event_time ON authentication_audit(event_time);
CREATE INDEX idx_auth_audit_is_deleted ON authentication_audit(is_deleted);

-- ============================================================================
-- INITIAL DATA - Default Roles
-- ============================================================================

INSERT INTO roles (id, name, created_by, created_date, is_deleted) VALUES
    (gen_random_uuid(), 'USER', 'SYSTEM', CURRENT_TIMESTAMP, FALSE),
    (gen_random_uuid(), 'ADMIN', 'SYSTEM', CURRENT_TIMESTAMP, FALSE),
    (gen_random_uuid(), 'MODERATOR', 'SYSTEM', CURRENT_TIMESTAMP, FALSE);

-- ============================================================================
-- SAMPLE DATA (Optional - for testing)
-- ============================================================================

-- Sample User (password: Password123!)
-- Note: This is a BCrypt hash of "Password123!"
INSERT INTO users (id, username, name, password, created_by, created_date, is_deleted) VALUES
    (gen_random_uuid(), 'admin@example.com', 'System Administrator', 
     '$2a$10$XYZ123...', 'SYSTEM', CURRENT_TIMESTAMP, FALSE);

-- Assign ADMIN role to sample user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username = 'admin@example.com' AND r.name = 'ADMIN';

-- Sample phone for admin user
INSERT INTO phone (id, user_id, number, city_code, country_code, created_by, created_date, is_deleted)
SELECT gen_random_uuid(), u.id, '12345678', '123', '58', 'SYSTEM', CURRENT_TIMESTAMP, FALSE
FROM users u 
WHERE u.username = 'admin@example.com';

-- ============================================================================
-- COMMENTS ON TABLES
-- ============================================================================

COMMENT ON TABLE roles IS 'Stores user roles for role-based access control';
COMMENT ON TABLE users IS 'Main user account table with authentication credentials';
COMMENT ON TABLE user_roles IS 'Junction table for many-to-many user-role relationship';
COMMENT ON TABLE phone IS 'Phone numbers associated with user accounts';
COMMENT ON TABLE refresh_tokens IS 'JWT refresh tokens for maintaining user sessions';
COMMENT ON TABLE authentication_audit IS 'Audit trail for all authentication events';

-- ============================================================================
-- END OF SCHEMA
-- ============================================================================

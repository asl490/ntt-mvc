-- ============================================================================
-- Database Schema for NTT-MVC Authentication System (H2 Compatible)
-- ============================================================================
-- Description: H2-compatible database schema for user authentication system
-- Database: H2 Database
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
    id UUID PRIMARY KEY,
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
    id UUID PRIMARY KEY,
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
    id UUID PRIMARY KEY,
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
    id UUID PRIMARY KEY,
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
    id UUID PRIMARY KEY,
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
    (RANDOM_UUID(), 'USER', 'SYSTEM', CURRENT_TIMESTAMP, FALSE),
    (RANDOM_UUID(), 'ADMIN', 'SYSTEM', CURRENT_TIMESTAMP, FALSE),
    (RANDOM_UUID(), 'MODERATOR', 'SYSTEM', CURRENT_TIMESTAMP, FALSE);

-- ============================================================================
-- END OF SCHEMA
-- ============================================================================

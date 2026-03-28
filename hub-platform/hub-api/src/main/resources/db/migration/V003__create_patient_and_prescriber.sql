CREATE TABLE IF NOT EXISTS patient (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  date_of_birth DATE NOT NULL,
  gender VARCHAR(20),
  phone_mobile VARCHAR(20),
  phone_home VARCHAR(20),
  email VARCHAR(200),
  address_line1 VARCHAR(200),
  address_line2 VARCHAR(100),
  city VARCHAR(100),
  state CHAR(2),
  zip VARCHAR(10),
  ssn_last4 CHAR(4),
  preferred_language VARCHAR(50) DEFAULT 'en',
  preferred_contact_method VARCHAR(20) DEFAULT 'SMS',
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS prescriber (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  npi VARCHAR(10) UNIQUE,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  credential VARCHAR(50),
  practice_name VARCHAR(200),
  phone VARCHAR(20),
  fax VARCHAR(20),
  address_line1 VARCHAR(200),
  city VARCHAR(100),
  state CHAR(2),
  zip VARCHAR(10),
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Local Service Hub - Supabase Database Schema
-- Complete schema for Hisar district services, bookings, partners, and live location tracking

-- 1. Services Table
CREATE TABLE IF NOT EXISTS public.services (
    id TEXT PRIMARY KEY,
    category TEXT NOT NULL,
    name TEXT NOT NULL,
    price TEXT NOT NULL,
    description TEXT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Seed initial popular services
INSERT INTO public.services (id, category, name, price, description, is_active) VALUES
('ac_repair', 'Appliance Repair', 'AC Service & Repair', '₹399', 'Jet pump wet cleaning, gas pressure check, and cooling coil inspection for window & split ACs.', true),
('electrician', 'Home Maintenance', 'Electrician On-Demand', '₹149', 'Switchboard wiring, MCB tripping repair, ceiling fan installation, and appliance safety audit.', true),
('plumber', 'Home Maintenance', 'Plumbing & Water Leakage', '₹199', 'Tap repair, pipeline blockage clearing, water tank valve replacement, and bathroom fittings.', true),
('deep_cleaning', 'Home Maintenance', 'Full Home Deep Cleaning', '₹999', 'Eco-friendly machine scrubbing for floors, bathroom descaling, kitchen degreasing, and balcony wash.', true),
('ro_repair', 'Appliance Repair', 'RO Water Purifier Service', '₹299', 'Filter cartridge replacement, TDS level test, sediment filter cleaning, and UV lamp check.', true),
('pest_control', 'Home Maintenance', 'Pest Control Treatment', '₹499', 'Safe herbal anti-termite and cockroach gel treatment with long-term protection.', true),
('mason_labor', 'Construction', 'Mason & Construction Labor', '₹650', 'Experienced bricklayer, plaster worker, and skilled daily construction helpers in Hisar.', true),
('salon_women', 'Beauty & Salon', 'Salon for Women at Home', '₹499', 'Facial, cleanup, waxing, threading, and bridal grooming by certified beauticians at home.', true),
('grooming_men', 'Beauty & Salon', 'Men Grooming & Haircut', '₹199', 'Hygienic home haircut, beard trimming, detan scrub, and relaxing scalp massage.', true)
ON CONFLICT (id) DO NOTHING;

-- 2. Partners Table
CREATE TABLE IF NOT EXISTS public.partners (
    id TEXT PRIMARY KEY DEFAULT gen_random_uuid()::text,
    name TEXT NOT NULL,
    phone TEXT NOT NULL,
    service_category TEXT NOT NULL,
    rating DOUBLE PRECISION NOT NULL DEFAULT 4.8,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    latitude DOUBLE PRECISION NOT NULL DEFAULT 29.1492,
    longitude DOUBLE PRECISION NOT NULL DEFAULT 75.7217,
    verification_status TEXT NOT NULL DEFAULT 'Verified'
);

-- Seed demo partners in Hisar
INSERT INTO public.partners (id, name, phone, service_category, rating, is_available, latitude, longitude, verification_status) VALUES
('partner_hisar_01', 'Rajesh Sharma', '+91 98120 11223', 'Appliance Repair', 4.9, true, 29.1534, 75.7225, 'Verified'),
('partner_hisar_02', 'Suresh Kumar', '+91 94160 44556', 'Home Maintenance', 4.8, true, 29.1620, 75.7150, 'Verified'),
('partner_hisar_03', 'Sunita Rani', '+91 98135 77889', 'Beauty & Salon', 5.0, true, 29.1420, 75.7310, 'Verified')
ON CONFLICT (id) DO NOTHING;

-- 3. Partner Locations (Live moving telemetry)
CREATE TABLE IF NOT EXISTS public.partner_locations (
    id TEXT PRIMARY KEY DEFAULT gen_random_uuid()::text,
    partner_id TEXT NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- Seed location for partner_hisar_01 near Hisar center
INSERT INTO public.partner_locations (id, partner_id, latitude, longitude, updated_at) VALUES
('loc_p1', 'partner_hisar_01', 29.1550, 75.7250, now())
ON CONFLICT (id) DO NOTHING;

-- 4. Bookings Table
CREATE TABLE IF NOT EXISTS public.bookings (
    id BIGINT PRIMARY KEY,
    name TEXT NOT NULL,
    phone TEXT NOT NULL,
    address TEXT NOT NULL,
    block TEXT NOT NULL,
    service_name TEXT NOT NULL,
    scheduled_time TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'Pending',
    provider_status TEXT NOT NULL DEFAULT 'Finding Partner',
    payment_status TEXT NOT NULL DEFAULT 'Pending',
    latitude DOUBLE PRECISION NOT NULL DEFAULT 29.1492,
    longitude DOUBLE PRECISION NOT NULL DEFAULT 75.7217,
    razorpay_payment_id TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 5. Enable Row Level Security (RLS) & Public Policies for Demo/App access
ALTER TABLE public.services ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.partners ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.partner_locations ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.bookings ENABLE ROW LEVEL SECURITY;

-- Allow public read & write for app client
CREATE POLICY "Allow public select services" ON public.services FOR SELECT USING (true);
CREATE POLICY "Allow public select partners" ON public.partners FOR SELECT USING (true);
CREATE POLICY "Allow public read-write partner_locations" ON public.partner_locations FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "Allow public read-write bookings" ON public.bookings FOR ALL USING (true) WITH CHECK (true);

-- Enable Supabase Realtime Publication for live updates
ALTER PUBLICATION supabase_realtime ADD TABLE public.bookings;
ALTER PUBLICATION supabase_realtime ADD TABLE public.partner_locations;

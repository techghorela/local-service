package com.example.data.local

object InitialData {
    val defaultServices = listOf(
        // Appliance Repair
        ServiceEntity(
            id = "app_ac_service",
            category = "Appliance Repair",
            name = "AC Service",
            price = "₹399",
            priceRange = "₹399 - ₹599",
            description = "High-pressure jet spray cleaning, cooling coil foam wash, filter sanitization, drain tray de-clogging and refrigerant gas pressure inspection.",
            iconName = "ac_unit",
            rating = 4.88,
            reviewCount = 380,
            isPopular = true
        ),
        ServiceEntity(
            id = "app_ac_install",
            category = "Appliance Repair",
            name = "AC Installation",
            price = "₹899",
            priceRange = "₹899 - ₹1,499",
            description = "Safe wall bracket mounting for indoor and outdoor units, copper pipe flare connection, vacuum leak testing and wiring hookup.",
            iconName = "build",
            rating = 4.82,
            reviewCount = 210,
            isPopular = false
        ),
        ServiceEntity(
            id = "app_ac_gas",
            category = "Appliance Repair",
            name = "AC Gas Refill",
            price = "₹1499",
            priceRange = "₹1,499 - ₹2,200",
            description = "Nitrogen pressure leak detection, valve brazing repair, vacuum purging and 100% genuine R32/R410A refrigerant charging.",
            iconName = "air",
            rating = 4.90,
            reviewCount = 195,
            isPopular = false
        ),
        ServiceEntity(
            id = "app_tv_repair",
            category = "Appliance Repair",
            name = "TV/LED Repair",
            price = "₹299",
            priceRange = "₹299 - ₹999",
            description = "Detailed on-site diagnostics for display backlighting, power supply board, motherboard repair, HDMI/sound ports and wall mounting.",
            iconName = "tv",
            rating = 4.79,
            reviewCount = 145,
            isPopular = false
        ),
        ServiceEntity(
            id = "app_fridge_repair",
            category = "Appliance Repair",
            name = "Fridge Repair",
            price = "₹499",
            priceRange = "₹499 - ₹1,299",
            description = "Single/Double door refrigerator cooling issue resolution, thermostat replacement, gas leakage repair, relay overload fix and gasket seal replacement.",
            iconName = "kitchen",
            rating = 4.85,
            reviewCount = 260,
            isPopular = true
        ),
        ServiceEntity(
            id = "app_ro_service",
            category = "Appliance Repair",
            name = "RO Service",
            price = "₹349",
            priceRange = "₹349 - ₹899",
            description = "Complete RO + UV + UF water purifier servicing, sediment & carbon filter replacement, membrane flushing, pump check and digital TDS verification.",
            iconName = "water_drop",
            rating = 4.92,
            reviewCount = 410,
            isPopular = true
        ),

        // Home Maintenance
        ServiceEntity(
            id = "maint_electrician",
            category = "Home Maintenance",
            name = "Electrician",
            price = "₹199",
            priceRange = "₹199 - ₹499",
            description = "Certified electrician for ceiling fan installation, MCB tripping repair, inverter connection, chandelier fitting and complete switchboard fixes.",
            iconName = "bolt",
            rating = 4.87,
            reviewCount = 520,
            isPopular = true
        ),
        ServiceEntity(
            id = "maint_plumber",
            category = "Home Maintenance",
            name = "Plumber",
            price = "₹249",
            priceRange = "₹249 - ₹699",
            description = "Expert plumbing for water leakage repair, tap/cock replacement, bathroom fittings, underground pipeline blockage and overhead water tank float valve setup.",
            iconName = "plumbing",
            rating = 4.84,
            reviewCount = 480,
            isPopular = true
        ),
        ServiceEntity(
            id = "maint_pest_control",
            category = "Home Maintenance",
            name = "Pest Control",
            price = "₹799",
            priceRange = "₹799 - ₹1,699",
            description = "Odorless herbal and chemical gel treatment for cockroaches, anti-termite drill-fill-seal protection, bedbugs eradication and rodent control with warranty.",
            iconName = "pest_control",
            rating = 4.78,
            reviewCount = 180,
            isPopular = false
        ),
        ServiceEntity(
            id = "maint_deep_cleaning",
            category = "Home Maintenance",
            name = "Deep Cleaning",
            price = "₹1499",
            priceRange = "₹1,499 - ₹3,499",
            description = "Intense deep cleaning for bathrooms, kitchen tiles, chimney degreasing, sofa shampooing, balcony wash and machine floor scrubbing.",
            iconName = "cleaning_services",
            rating = 4.91,
            reviewCount = 330,
            isPopular = true
        ),

        // Construction
        ServiceEntity(
            id = "const_mason",
            category = "Construction",
            name = "Mason",
            price = "₹800",
            priceRange = "₹800/day",
            description = "Experienced Rajmistri for high precision brickwork, wall plastering, flooring tiles, boundary wall work, lintel beams and home remodeling.",
            iconName = "foundation",
            rating = 4.83,
            reviewCount = 150,
            isPopular = true
        ),
        ServiceEntity(
            id = "const_labor",
            category = "Construction",
            name = "Labor",
            price = "₹500",
            priceRange = "₹500/day",
            description = "Dedicated and verified construction beldar/helpers for cement mortar mixing, brick shifting, debris removal and site assistance.",
            iconName = "engineering",
            rating = 4.76,
            reviewCount = 135,
            isPopular = false
        ),
        ServiceEntity(
            id = "const_painter",
            category = "Construction",
            name = "Painter",
            price = "₹25/sq.ft",
            priceRange = "₹25 - ₹45/sq.ft",
            description = "Professional interior and exterior painting, wall crack putty treatment, primer application, texture designs and water-resistant premium emulsion.",
            iconName = "format_paint",
            rating = 4.89,
            reviewCount = 220,
            isPopular = true
        ),

        // Beauty & Salon
        ServiceEntity(
            id = "beauty_women_salon",
            category = "Beauty & Salon",
            name = "Women's Salon",
            price = "₹499",
            priceRange = "₹499 - ₹1,899",
            description = "Salon at home by certified beauticians: Honey waxing, herbal facial, eyebrow threading, fruit bleach, aroma manicure and luxury spa pedicure.",
            iconName = "face_retouching_natural",
            rating = 4.95,
            reviewCount = 560,
            isPopular = true
        ),
        ServiceEntity(
            id = "beauty_men_grooming",
            category = "Beauty & Salon",
            name = "Men's Grooming",
            price = "₹299",
            priceRange = "₹299 - ₹799",
            description = "At-home hygienic haircut, beard styling & trimming, face detan scrub, blackhead removal and relaxing head & neck massage.",
            iconName = "content_cut",
            rating = 4.86,
            reviewCount = 390,
            isPopular = true
        )
    )
}

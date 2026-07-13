


-- ===== DAIRY =====
INSERT INTO products (id, name, category, brand, price, size, is_seasonal, is_available, image_url) VALUES
(1, 'Whole Milk', 'Dairy', 'Amul', 60.00, '1 liter', false, true, null),
(2, 'Toned Milk', 'Dairy', 'Amul', 52.00, '1 liter', false, true, null),
(3, 'Butter', 'Dairy', 'Amul', 56.00, '100g', false, true, null),
(4, 'Cheddar Cheese', 'Dairy', 'Amul', 120.00, '200g', false, true, null),
(5, 'Paneer', 'Dairy', 'Amul', 90.00, '200g', false, true, null),
(6, 'Yogurt', 'Dairy', 'Nestle', 30.00, '200g', false, true, null),
(7, 'Cream', 'Dairy', 'Amul', 45.00, '200ml', false, true, null),
(8, 'Ghee', 'Dairy', 'Amul', 560.00, '1 liter', false, true, null),
(9, 'Almond Milk', 'Dairy', 'Sofit', 99.00, '1 liter', false, true, null),
(10, 'Buttermilk', 'Dairy', 'Amul', 25.00, '200ml', false, true, null),
(11, 'Condensed Milk', 'Dairy', 'Milkmaid', 110.00, '400g', false, true, null),
(12, 'Cottage Cheese', 'Dairy', 'Mother Dairy', 85.00, '200g', false, true, null)
ON CONFLICT (id) DO NOTHING;


INSERT INTO products (id, name, category, brand, price, size, is_seasonal, is_available, image_url) VALUES
(13, 'Apples', 'Fruits', 'Fresh', 180.00, '1 kg', false, true, null),
(14, 'Bananas', 'Fruits', 'Fresh', 50.00, '1 dozen', false, true, null),
(15, 'Oranges', 'Fruits', 'Fresh', 80.00, '1 kg', true, true, null),
(16, 'Mangoes', 'Fruits', 'Fresh', 200.00, '1 kg', true, true, null),
(17, 'Grapes', 'Fruits', 'Fresh', 120.00, '500g', true, true, null),
(18, 'Watermelon', 'Fruits', 'Fresh', 40.00, '1 piece', true, true, null),
(19, 'Pineapple', 'Fruits', 'Fresh', 60.00, '1 piece', true, true, null),
(20, 'Strawberries', 'Fruits', 'Fresh', 150.00, '250g', true, true, null),
(21, 'Pomegranate', 'Fruits', 'Fresh', 160.00, '1 kg', true, true, null),
(22, 'Papaya', 'Fruits', 'Fresh', 45.00, '1 piece', false, true, null),
(23, 'Guava', 'Fruits', 'Fresh', 60.00, '500g', true, true, null),
(24, 'Kiwi', 'Fruits', 'Zespri', 180.00, '3 pieces', false, true, null)
ON CONFLICT (id) DO NOTHING;

-- ===== VEGETABLES =====
INSERT INTO products (id, name, category, brand, price, size, is_seasonal, is_available, image_url) VALUES
(25, 'Tomatoes', 'Vegetables', 'Fresh', 40.00, '1 kg', false, true, null),
(26, 'Onions', 'Vegetables', 'Fresh', 35.00, '1 kg', false, true, null),
(27, 'Potatoes', 'Vegetables', 'Fresh', 30.00, '1 kg', false, true, null),
(28, 'Carrots', 'Vegetables', 'Fresh', 45.00, '500g', false, true, null),
(29, 'Spinach', 'Vegetables', 'Fresh', 30.00, '1 bunch', false, true, null),
(30, 'Broccoli', 'Vegetables', 'Fresh', 70.00, '1 piece', false, true, null),
(31, 'Capsicum', 'Vegetables', 'Fresh', 80.00, '500g', false, true, null),
(32, 'Cauliflower', 'Vegetables', 'Fresh', 40.00, '1 piece', false, true, null),
(33, 'Green Peas', 'Vegetables', 'Fresh', 60.00, '500g', true, true, null),
(34, 'Cucumber', 'Vegetables', 'Fresh', 30.00, '500g', false, true, null),
(35, 'Garlic', 'Vegetables', 'Fresh', 50.00, '250g', false, true, null),
(36, 'Ginger', 'Vegetables', 'Fresh', 60.00, '250g', false, true, null),
(37, 'Green Chillies', 'Vegetables', 'Fresh', 20.00, '100g', false, true, null),
(38, 'Mushrooms', 'Vegetables', 'Fresh', 90.00, '200g', false, true, null),
(39, 'Sweet Corn', 'Vegetables', 'Fresh', 35.00, '2 pieces', true, true, null)
ON CONFLICT (id) DO NOTHING;

-- ===== BAKERY =====
INSERT INTO products (id, name, category, brand, price, size, is_seasonal, is_available, image_url) VALUES
(40, 'White Bread', 'Bakery', 'Britannia', 45.00, '400g', false, true, null),
(41, 'Brown Bread', 'Bakery', 'Britannia', 50.00, '400g', false, true, null),
(42, 'Multigrain Bread', 'Bakery', 'Britannia', 60.00, '400g', false, true, null),
(43, 'Burger Buns', 'Bakery', 'Britannia', 40.00, '4 pieces', false, true, null),
(44, 'Croissants', 'Bakery', 'Fresho', 120.00, '4 pieces', false, true, null),
(45, 'Pav', 'Bakery', 'Local', 30.00, '6 pieces', false, true, null),
(46, 'Cake Rusk', 'Bakery', 'Britannia', 55.00, '200g', false, true, null),
(47, 'Cookies', 'Bakery', 'Parle', 30.00, '200g', false, true, null)
ON CONFLICT (id) DO NOTHING;

-- ===== BEVERAGES =====
INSERT INTO products (id, name, category, brand, price, size, is_seasonal, is_available, image_url) VALUES
(48, 'Orange Juice', 'Beverages', 'Tropicana', 95.00, '1 liter', false, true, null),
(49, 'Apple Juice', 'Beverages', 'Real', 110.00, '1 liter', false, true, null),
(50, 'Green Tea', 'Beverages', 'Lipton', 180.00, '25 bags', false, true, null),
(51, 'Coffee Powder', 'Beverages', 'Nescafe', 350.00, '200g', false, true, null),
(52, 'Tea Leaves', 'Beverages', 'Tata Tea', 220.00, '500g', false, true, null),
(53, 'Coca Cola', 'Beverages', 'Coca Cola', 40.00, '750ml', false, true, null),
(54, 'Mineral Water', 'Beverages', 'Bisleri', 20.00, '1 liter', false, true, null),
(55, 'Mango Juice', 'Beverages', 'Maaza', 30.00, '600ml', true, true, null),
(56, 'Coconut Water', 'Beverages', 'Paper Boat', 35.00, '200ml', false, true, null),
(57, 'Lassi', 'Beverages', 'Amul', 30.00, '200ml', false, true, null),
(58, 'Energy Drink', 'Beverages', 'Red Bull', 125.00, '250ml', false, true, null)
ON CONFLICT (id) DO NOTHING;

-- ===== SNACKS =====
INSERT INTO products (id, name, category, brand, price, size, is_seasonal, is_available, image_url) VALUES
(59, 'Potato Chips', 'Snacks', 'Lays', 30.00, '90g', false, true, null),
(60, 'Namkeen Mix', 'Snacks', 'Haldirams', 60.00, '200g', false, true, null),
(61, 'Biscuits', 'Snacks', 'Parle-G', 10.00, '80g', false, true, null),
(62, 'Chocolate', 'Snacks', 'Dairy Milk', 50.00, '50g', false, true, null),
(63, 'Peanuts', 'Snacks', 'Haldirams', 45.00, '200g', false, true, null),
(64, 'Popcorn', 'Snacks', 'Act II', 40.00, '70g', false, true, null),
(65, 'Muesli', 'Snacks', 'Kelloggs', 350.00, '500g', false, true, null),
(66, 'Protein Bar', 'Snacks', 'RiteBite', 80.00, '40g', false, true, null),
(67, 'Kurkure', 'Snacks', 'Kurkure', 20.00, '75g', false, true, null),
(68, 'Nachos', 'Snacks', 'Doritos', 60.00, '75g', false, true, null),
(69, 'Dry Fruits Mix', 'Snacks', 'Happilo', 450.00, '400g', false, true, null)
ON CONFLICT (id) DO NOTHING;

-- ===== MEAT & SEAFOOD =====
INSERT INTO products (id, name, category, brand, price, size, is_seasonal, is_available, image_url) VALUES
(70, 'Chicken Breast', 'Meat', 'Fresho', 280.00, '500g', false, true, null),
(71, 'Chicken Drumstick', 'Meat', 'Fresho', 200.00, '500g', false, true, null),
(72, 'Eggs', 'Meat', 'Farm Fresh', 80.00, '12 pieces', false, true, null),
(73, 'Mutton', 'Meat', 'Fresho', 650.00, '500g', false, true, null),
(74, 'Fish (Rohu)', 'Meat', 'Fresh', 250.00, '500g', false, true, null),
(75, 'Prawns', 'Meat', 'Fresh', 450.00, '500g', false, true, null),
(76, 'Sausages', 'Meat', 'Venkys', 160.00, '250g', false, true, null)
ON CONFLICT (id) DO NOTHING;

-- ===== FROZEN FOODS =====
INSERT INTO products (id, name, category, brand, price, size, is_seasonal, is_available, image_url) VALUES
(77, 'Frozen Peas', 'Frozen', 'Safal', 80.00, '500g', false, true, null),
(78, 'Frozen Mixed Vegetables', 'Frozen', 'Safal', 110.00, '500g', false, true, null),
(79, 'Ice Cream (Vanilla)', 'Frozen', 'Amul', 120.00, '500ml', false, true, null),
(80, 'Frozen Parathas', 'Frozen', 'McCain', 100.00, '5 pieces', false, true, null),
(81, 'Frozen French Fries', 'Frozen', 'McCain', 130.00, '450g', false, true, null),
(82, 'Frozen Nuggets', 'Frozen', 'McCain', 180.00, '300g', false, true, null),
(83, 'Kulfi', 'Frozen', 'Amul', 60.00, '4 pieces', true, true, null)
ON CONFLICT (id) DO NOTHING;

-- ===== GRAINS & STAPLES =====
INSERT INTO products (id, name, category, brand, price, size, is_seasonal, is_available, image_url) VALUES
(84, 'Basmati Rice', 'Grains', 'India Gate', 180.00, '1 kg', false, true, null),
(85, 'Wheat Flour (Atta)', 'Grains', 'Aashirvaad', 280.00, '5 kg', false, true, null),
(86, 'Toor Dal', 'Grains', 'Tata', 140.00, '1 kg', false, true, null),
(87, 'Moong Dal', 'Grains', 'Tata', 130.00, '1 kg', false, true, null),
(88, 'Chana Dal', 'Grains', 'Tata', 110.00, '1 kg', false, true, null),
(89, 'Sugar', 'Grains', 'Local', 45.00, '1 kg', false, true, null),
(90, 'Salt', 'Grains', 'Tata', 25.00, '1 kg', false, true, null),
(91, 'Oats', 'Grains', 'Quaker', 120.00, '500g', false, true, null),
(92, 'Pasta', 'Grains', 'Barilla', 110.00, '500g', false, true, null),
(93, 'Noodles', 'Grains', 'Maggi', 14.00, '70g', false, true, null),
(94, 'Rajma', 'Grains', 'Tata', 150.00, '1 kg', false, true, null),
(95, 'Poha', 'Grains', 'Local', 55.00, '500g', false, true, null)
ON CONFLICT (id) DO NOTHING;

-- ===== HOUSEHOLD =====
INSERT INTO products (id, name, category, brand, price, size, is_seasonal, is_available, image_url) VALUES
(96, 'Dish Soap', 'Household', 'Vim', 35.00, '250ml', false, true, null),
(97, 'Laundry Detergent', 'Household', 'Surf Excel', 260.00, '1 kg', false, true, null),
(98, 'Floor Cleaner', 'Household', 'Lizol', 160.00, '500ml', false, true, null),
(99, 'Kitchen Towel', 'Household', 'Scott', 90.00, '2 rolls', false, true, null),
(100, 'Garbage Bags', 'Household', 'Ezee', 85.00, '30 pieces', false, true, null),
(101, 'Aluminium Foil', 'Household', 'Freshwrap', 110.00, '9m', false, true, null),
(102, 'Cling Wrap', 'Household', 'Freshwrap', 95.00, '30m', false, true, null),
(103, 'Scrub Pad', 'Household', 'Scotch Brite', 40.00, '3 pieces', false, true, null)
ON CONFLICT (id) DO NOTHING;

-- ===== PERSONAL CARE =====
INSERT INTO products (id, name, category, brand, price, size, is_seasonal, is_available, image_url) VALUES
(104, 'Toothpaste', 'Personal Care', 'Colgate', 85.00, '150g', false, true, null),
(105, 'Shampoo', 'Personal Care', 'Head & Shoulders', 220.00, '340ml', false, true, null),
(106, 'Soap', 'Personal Care', 'Dove', 55.00, '100g', false, true, null),
(107, 'Hand Wash', 'Personal Care', 'Dettol', 99.00, '200ml', false, true, null),
(108, 'Face Wash', 'Personal Care', 'Himalaya', 130.00, '150ml', false, true, null),
(109, 'Deodorant', 'Personal Care', 'Nivea', 200.00, '150ml', false, true, null),
(110, 'Tissue Paper', 'Personal Care', 'Kleenex', 75.00, '100 sheets', false, true, null)
ON CONFLICT (id) DO NOTHING;

-- ===== SPICES & CONDIMENTS =====
INSERT INTO products (id, name, category, brand, price, size, is_seasonal, is_available, image_url) VALUES
(111, 'Cooking Oil', 'Spices', 'Fortune', 170.00, '1 liter', false, true, null),
(112, 'Mustard Oil', 'Spices', 'Patanjali', 160.00, '1 liter', false, true, null),
(113, 'Olive Oil', 'Spices', 'Figaro', 350.00, '500ml', false, true, null),
(114, 'Turmeric Powder', 'Spices', 'Everest', 65.00, '100g', false, true, null),
(115, 'Red Chilli Powder', 'Spices', 'Everest', 70.00, '100g', false, true, null),
(116, 'Garam Masala', 'Spices', 'MDH', 80.00, '100g', false, true, null),
(117, 'Cumin Seeds', 'Spices', 'Everest', 90.00, '100g', false, true, null),
(118, 'Tomato Ketchup', 'Spices', 'Kissan', 110.00, '500g', false, true, null),
(119, 'Soy Sauce', 'Spices', 'Chings', 55.00, '200ml', false, true, null),
(120, 'Vinegar', 'Spices', 'Nilons', 40.00, '200ml', false, true, null),
(121, 'Honey', 'Spices', 'Dabur', 280.00, '500g', false, true, null),
(122, 'Pickle (Mango)', 'Spices', 'Mothers Recipe', 95.00, '300g', true, true, null)
ON CONFLICT (id) DO NOTHING;

-- ===== DEFAULT SHOPPING LIST =====
INSERT INTO shopping_lists (id, name, is_active, created_at, updated_at) VALUES
(1, 'My Shopping List', true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- ===== POPULATE PRODUCT IMAGES =====
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1550583724-b2692b85b150?auto=format&fit=crop&w=300&q=80' WHERE category = 'Dairy';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1619566636858-adf3ef46400b?auto=format&fit=crop&w=300&q=80' WHERE category = 'Fruits';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1566385101042-1a0aa0c1268c?auto=format&fit=crop&w=300&q=80' WHERE category = 'Vegetables';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=300&q=80' WHERE category = 'Bakery';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1534080391025-097b03b77385?auto=format&fit=crop&w=300&q=80' WHERE category = 'Beverages';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1599490659213-e2b9527b0876?auto=format&fit=crop&w=300&q=80' WHERE category = 'Snacks';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1607623814075-e51df1bdc82f?auto=format&fit=crop&w=300&q=80' WHERE category = 'Meat';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1516559828984-fb3b9952c64b?auto=format&fit=crop&w=300&q=80' WHERE category = 'Frozen';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1574316071802-0d684efa7bf5?auto=format&fit=crop&w=300&q=80' WHERE category = 'Grains';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1581578731548-c64695cc6952?auto=format&fit=crop&w=300&q=80' WHERE category = 'Household';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1556228720-195a672e8a03?auto=format&fit=crop&w=300&q=80' WHERE category = 'Personal Care';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1596797038530-2c107229654b?auto=format&fit=crop&w=300&q=80' WHERE category = 'Spices';

-- ===== RESET SEQUENCES =====
-- When inserting with explicit IDs, PostgreSQL's identity sequences don't advance.
-- This resets them to max(id)+1 so that new inserts don't collide with seeded data.
SELECT setval(pg_get_serial_sequence('products', 'id'), COALESCE(MAX(id), 1)) FROM products;
SELECT setval(pg_get_serial_sequence('shopping_lists', 'id'), COALESCE(MAX(id), 1)) FROM shopping_lists;
SELECT setval(pg_get_serial_sequence('list_items', 'id'), COALESCE(MAX(id), 1)) FROM list_items;
SELECT setval(pg_get_serial_sequence('purchase_history', 'id'), COALESCE(MAX(id), 1)) FROM purchase_history;


user {
	password varchar(255)
	is_organization boolean
	id int pk increments
	username varchar(30) unique
	created_at timestamp
}

category {
	id int pk increments
	name varchar(50) unique
}

listing {
	id int pk increments
	user_id int > user.id
	category_id int > category.id
	type varchar(10)
	quantity int
	quantity_remaining int
	urgency varchar(10) null
	needed_by date null
	available_until date null
	status varchar(20)
	created_at timestamp
}

match_line {
	id int pk increments
	need_listing_id int > listing.id
	surplus_listing_id int > listing.id
	quantity_matched int
	matched_at timestamp
}


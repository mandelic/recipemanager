CREATE TABLE recipe (
   id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
   name TEXT,
   description TEXT,
   created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
   user_id UUID REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE component (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    recipe_id UUID REFERENCES recipe(id) ON DELETE CASCADE,
    name TEXT
);

CREATE TABLE ingredient (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    component_id UUID REFERENCES component(id) ON DELETE CASCADE,
    name TEXT,
    quantity FLOAT,
    unit TEXT
);

CREATE TABLE step (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    component_id UUID REFERENCES component(id) ON DELETE CASCADE,
    step_number NUMERIC,
    description TEXT
);
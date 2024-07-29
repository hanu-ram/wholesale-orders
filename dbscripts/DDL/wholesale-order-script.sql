drop table if exists order_details;
CREATE TABLE IF NOT EXISTS order_details
(
    sales_document_number character varying(255) NOT NULL,
    sold_to character varying(255),
    sales_document_type character varying(255),
    sales_org character varying(255),
    ship_to character varying(255),
    sales_document_date timestamp without time zone,
    purchase_order_number character varying(255),
    purchase_order_type character varying(255),
    customer_name character varying(255),
    delivery_date timestamp without time zone,
    amount double precision,
    country_code character varying(255),
    discount double precision,
    invoice_number character varying(255),
    order_reason character varying(255),
    order_status character varying(255),
    planning_group character varying(255),
    region character varying(255),
    tax double precision,
    tracking_number character varying(255),
    is_valid boolean,
    modify_user character varying(255),
    modify_ts timestamp without time zone,
    create_ts timestamp without time zone,
    create_user character varying(255),
    CONSTRAINT order_details_pkey PRIMARY KEY (sales_document_number)
);

drop table if exists line_entry;
CREATE TABLE IF NOT EXISTS line_entry
(
    line_entry_id character varying(255) NOT NULL,
    sales_document_number character varying(255),
    material_code character varying(255) NOT NULL,
    material_name character varying(255),
    line_item character varying(255),
    line_item_status character varying(255),
    brand character varying(255),
    cancelled_quantity numeric (20, 3),
    confirmed_quantity numeric (20, 3),
    consumer_group character varying(255),
    currency character varying(255),
    customer_expc_msrp double precision,
    del_ind boolean,
    delivery_date timestamp without time zone,
    description character varying(255) ,
    discounts double precision,
    expected_price_edi double precision,
    gross_value double precision,
    item_category character varying(255),
    levi_retail_price double precision,
    net_price double precision,
    net_value double precision,
    plant character varying(255),
    quantity numeric(20, 3),
    rpm_price double precision,
    ship_to character varying(255),
    shipped_quantity numeric (20, 3),
    stock_type character varying(255),
    sub_total double precision,
    wholesale_price double precision,
    wholesale_price_valid_from timestamp without time zone,
    wholesale_price_valid_to timestamp without time zone,
    modify_user character varying(255),
    modify_ts timestamp without time zone,
    create_ts timestamp without time zone,
    create_user character varying(255),
    constraint line_entry_pkey primary key (line_entry_id),
    constraint fk_order foreign key (sales_document_number) references order_details(sales_document_number)
);

drop table if exists schedule_line_entry;
CREATE TABLE IF NOT EXISTS schedule_line_entry
(
    "id" character varying(255) NOT NULL,
    sales_document_number character varying(255),
    material_code character varying(255),
    line_entry_id character varying(255),
    line_item character varying(255),
    sch_line_item character varying(255),
    approx_due_in_date timestamp without time zone,
    cancel_date timestamp without time zone,
    cancelled_sku_qty numeric (20, 3),
    confirmed_quantity numeric (20, 3),
    del_ind boolean,
    delivery_date timestamp without time zone,
    fixed_qty numeric (20, 3),
    invoice_date timestamp without time zone,
    invoice_doc character varying(255) ,
    open_qty numeric (20, 3),
    ordered_quantity numeric (20, 3),
    pac_unconfirmed_quantity numeric (20, 3),
    vir_unconfirmed_quantity numeric (20, 3),
    rejection_reason_code character varying(255) ,
    rejection_reason_description character varying(255),
    requested_delivery_date timestamp without time zone,
    schedule_status character varying(255),
    ship_date timestamp without time zone,
    ship_to character varying(255),
    shipped_quantity numeric (20, 3),
    "size" character varying(255),
    store_name character varying(255),
    unit_of_measurement character varying(255),
    modify_user character varying(255) ,
    modify_ts timestamp without time zone,
    create_ts timestamp without time zone,
    create_user character varying(255),
    constraint schedule_line_entry_pkey primary key (id),
    constraint fk_line foreign key (line_entry_id) references line_entry(line_entry_id)
);

drop table if exists error_details;
CREATE TABLE IF NOT EXISTS error_details
(
    "id" character varying(255) NOT NULL,
    sales_document_number character varying(255),
    material_code character varying(255),
    line_item character varying(255),
    sch_line_item character varying(255),
    schedule_status character varying(255),
    ship_date timestamp without time zone,
    ship_to character varying(255),
    "size" character varying(255),
    sold_to character varying(255),
    stock_type character varying(255),
    store_name character varying(255),
    line_item_status character varying(255),
    sales_document_type character varying(255),
    amount double precision,
    approx_due_in_date timestamp without time zone,
    brand character varying(255),
    cancel_date timestamp without time zone,
    cancelled_quantity numeric (20, 3),
    cancelled_sku_qty numeric (20, 3),
    confirmed_quantity numeric (20, 3),
    consumer_group character varying(255),
    country_code character varying(255),
    currency character varying(255),
    customer_expc_msrp double precision,
    customer_name character varying(255),
    del_ind boolean,
    delivery_date timestamp without time zone,
    description character varying(255),
    discount double precision,
    discounts double precision,
    expected_price_edi double precision,
    fixed_qty numeric (20, 3),
    gross_value double precision,
    invoice_date timestamp without time zone,
    invoice_doc character varying(255),
    invoice_number character varying(255),
    is_processed boolean,
    item_category character varying(255),
    levi_retail_price double precision,
    material_name character varying(255),
    net_price double precision,
    net_value double precision,
    open_qty numeric (20, 3),
    order_status character varying(255),
    ordered_quantity numeric (20, 3),
    shipped_quantity numeric (20, 3),
    pac_unconfirmed_quantity numeric (20, 3),
    vir_unconfirmed_quantity numeric (20, 3),
    planning_group character varying(255),
    plant character varying(255),
    purchase_order_number character varying(255),
    purchase_order_type character varying(255),
    quantity numeric (20, 3),
    region character varying(255),
    rejection_reason_code character varying(255),
    rejection_reason_description character varying(255),
    requested_delivery_date timestamp without time zone,
    rpm_price double precision,
    sales_document_date timestamp without time zone,
    sales_org character varying(255),
    sub_total double precision,
    tax double precision,
    tracking_number character varying(255),
    unit_of_measurement character varying(255),
    wholesale_price double precision,
    wholesale_price_valid_from timestamp without time zone,
    wholesale_price_valid_to timestamp without time zone,
    error_msg character varying(255),
    order_reason character varying(255),
    modify_user character varying(255),
    modify_ts timestamp without time zone,
    create_ts timestamp without time zone,
    create_user character varying(255),
    CONSTRAINT error_details_pkey PRIMARY KEY (id)
);

drop table if exists job_execution_status;
create table if not exists job_execution_status (
    job_name varchar(50)  NOT NULL,
    last_run   timestamp,
    date_of_run  timestamp,
    constraint job_name_pk primary key (job_name)
);

insert into job_execution_status values ('pricing_job', null, null);
insert into job_execution_status values ('vir_job', null, null);
insert into job_execution_status values ('pac_job', null, null);

DROP INDEX IF EXISTS idx_schedule_line_entry_sales_document_number_and_line_entry_id;
create index idx_schedule_line_entry_sales_document_number_and_line_entry_id
on schedule_line_entry(sales_document_number, line_entry_id);

DROP INDEX IF EXISTS order_details_sales_document_date_and_number_and_modify_ts;
create index order_details_sales_document_date_and_number_and_modify_ts
on order_details(sales_document_number, sales_document_date, modify_ts);

DROP INDEX IF EXISTS idx_line_entry_pricing_filter;
create index idx_line_entry_pricing_filter
on line_entry(sales_document_number, modify_ts, discounts, gross_value, wholesale_price, net_value);

DROP INDEX IF EXISTS error_details_sales_document_number;
create index error_details_sales_document_number
on error_details(sales_document_number);

DROP INDEX IF EXISTS schedule_line_entry_modify_ts;
create index schedule_line_entry_modify_ts
on schedule_line_entry(modify_ts);
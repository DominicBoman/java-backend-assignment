--
-- PostgreSQL database dump
--

-- Dumped from database version 17.2 (Debian 17.2-1.pgdg120+1)
-- Dumped by pg_dump version 17.2 (Debian 17.2-1.pgdg120+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE public.users_id_seq
	START WITH 1
	INCREMENT BY 1
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;


ALTER SEQUENCE public.users_id_seq OWNER TO "user";

--
-- Name: users; Type: TABLE; Schema: public; Owner: user
--

CREATE TABLE public.users (
	id bigint DEFAULT nextval('public.users_id_seq'::regclass) NOT NULL,
	username character varying(255) NOT NULL,
	password character varying(255) NOT NULL,
	first_name character varying(255),
	last_name character varying(255)
);


ALTER TABLE public.users OWNER TO "user";

--
-- Name: building_id_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE public.building_id_seq
	START WITH 1
	INCREMENT BY 1
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;


ALTER SEQUENCE public.building_id_seq OWNER TO "user";

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: building; Type: TABLE; Schema: public; Owner: user
--

CREATE TABLE public.building (
	id bigint DEFAULT nextval('public.building_id_seq'::regclass) NOT NULL,
	name character varying(255) NOT NULL,
	owner_id bigint NOT NULL,
	city character varying(255) NOT NULL,
	street character varying(255) NOT NULL,
	postal_code character varying(255) NOT NULL
);


ALTER TABLE public.building OWNER TO "user";

--
-- Name: zone_id_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE public.zone_id_seq
	START WITH 1
	INCREMENT BY 1
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;


ALTER SEQUENCE public.zone_id_seq OWNER TO "user";

--
-- Name: zone; Type: TABLE; Schema: public; Owner: user
--

CREATE TABLE public.zone (
	id bigint DEFAULT nextval('public.zone_id_seq'::regclass) NOT NULL,
	name character varying(255) NOT NULL,
	building_id bigint NOT NULL,
	current_temp numeric(5,2) DEFAULT 22.00 NOT NULL,
	target_temp numeric(5,2) DEFAULT 22.00,
	created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
	updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
	description character varying(255)
);


ALTER TABLE public.zone OWNER TO "user";

--
-- Name: building_id_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('public.building_id_seq', 12, true);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('public.users_id_seq', 18, true);


--
-- Name: zone_id_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('public.zone_id_seq', 20, true);


--
-- Name: building building_pkey; Type: CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.building
	ADD CONSTRAINT building_pkey PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.users
	ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: users users_username_key; Type: CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.users
	ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- Name: zone zone_pkey; Type: CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.zone
	ADD CONSTRAINT zone_pkey PRIMARY KEY (id);


--
-- Name: zone building; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.zone
	ADD CONSTRAINT building FOREIGN KEY (building_id) REFERENCES public.building(id) ON DELETE CASCADE;


--
-- Name: building owner; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.building
	ADD CONSTRAINT owner FOREIGN KEY (owner_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: CONSTRAINT owner ON building; Type: COMMENT; Schema: public; Owner: user
--

COMMENT ON CONSTRAINT owner ON public.building IS 'user id';


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: user
--

COPY public.users (id, username, password, first_name, last_name) FROM stdin;
14	user1	$2a$10$9BvODtUjg2WXsmSTRS1fsuZ/qrZKqLNGVhBI6lQRUhVCh2/wCRj56	John	Doe
18	user2	$2a$10$svBmMH9JmVJO/J/oRkzTNezjDx0kQ0ZNV2hM.gWGCkD3baKCdqVH6	Bobby	Anderson
\.

--
-- Data for Name: building; Type: TABLE DATA; Schema: public; Owner: user
--

COPY public.building (id, name, owner_id, city, street, postal_code) FROM stdin;
8	Juice Bar	14	Paris	Road 440	21331
9	Bar	14	Paris	Road 400	21331
11	Fish Market	14	Paris	Street 401	213144
12	Cafe	14	Tokyo	Street 302	4421412
10	Museum	14	Paris	Street 404	2131-2424
2	Tower	18	Stockholm	Street 33	11444
5	Supermarket	18	Stockholm	Street 22	11444
3	Shed	18	Trosa	Road 44	61933
6	Hotel	18	New York	Road 444	31233
\.


--
-- Data for Name: zone; Type: TABLE DATA; Schema: public; Owner: user
--

COPY public.zone (id, name, building_id, current_temp, target_temp, created_at, updated_at, description) FROM stdin;
9	Floor 1	6	20.00	20.00	2025-02-09 19:59:12.604662	2025-02-09 19:59:12.604662	\N
8	Hotel	6	20.00	20.00	2025-02-09 16:00:12.88442	2025-02-09 16:00:12.88442	\N
2	Entrance	2	20.00	20.00	2025-02-08 12:49:06.72486	2025-02-08 12:49:06.72486	\N
3	Main Floor	5	20.00	20.00	2025-02-08 12:49:06.72486	2025-02-08 12:49:06.72486	\N
4	Office	5	20.00	20.00	2025-02-08 12:49:06.72486	2025-02-08 12:49:06.72486	\N
6	Restroom	5	20.00	20.00	2025-02-08 14:20:26.294721	2025-02-08 14:20:26.294721	\N
7	Basement	5	20.00	20.00	2025-02-08 14:24:15.221253	2025-02-08 14:24:15.221253	\N
5	Basement	3	20.00	20.00	2025-02-08 14:18:45.82739	2025-02-08 14:18:45.82739	\N
14	Zone 0	8	1.00	1.00	2025-02-10 01:18:58.257041	2025-02-10 01:18:58.257041	\N
15	Room	3	20.00	20.00	2025-02-08 12:49:06.72486	2025-02-08 12:49:06.72486	\N
16	Office	11	20.00	20.00	2025-02-08 12:49:06.72486	2025-02-08 12:49:06.72486	\N
17	Office	12	20.00	20.00	2025-02-08 12:49:06.72486	2025-02-08 12:49:06.72486	\N
18	Kitchen	9	20.00	20.00	2025-02-08 12:49:06.72486	2025-02-08 12:49:06.72486	\N
19	Hall 1	10	1.00	1.00	2025-02-10 01:18:58.257041	2025-02-10 01:18:58.257041	\N
20	Hall 2	10	1.00	1.00	2025-02-10 01:18:58.257041	2025-02-10 01:18:58.257041	\N
13	Floor 2	9	1.00	1.00	2025-02-09 22:54:53.873343	2025-02-09 22:54:53.873343	\N
12	Floor 1	9	1.00	1.00	2025-02-09 22:54:52.706475	2025-02-09 22:54:52.706475	\N
\.


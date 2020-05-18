-- "D:\Programs\Postgers\bin\pg_dump.exe" -h localhost -U postgres -F p -f D:\pg.sql postgres
-- PostgreSQL database dump
--

-- Dumped from database version 12.2
-- Dumped by pg_dump version 12.2

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: chat; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA chat;


ALTER SCHEMA chat OWNER TO postgres;

--
-- Name: adminpack; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS adminpack WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION adminpack; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION adminpack IS 'administrative functions for PostgreSQL';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: blocklist; Type: TABLE; Schema: chat; Owner: postgres
--

CREATE TABLE chat.blocklist (
    id integer NOT NULL,
    who integer,
    whom integer
);


ALTER TABLE chat.blocklist OWNER TO postgres;

--
-- Name: blocklist_id_seq; Type: SEQUENCE; Schema: chat; Owner: postgres
--

ALTER TABLE chat.blocklist ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME chat.blocklist_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: main; Type: TABLE; Schema: chat; Owner: postgres
--

CREATE TABLE chat.main (
    id integer NOT NULL,
    login text,
    password text,
    nickname text
);


ALTER TABLE chat.main OWNER TO postgres;

--
-- Name: main_id_seq; Type: SEQUENCE; Schema: chat; Owner: postgres
--

ALTER TABLE chat.main ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME chat.main_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Data for Name: blocklist; Type: TABLE DATA; Schema: chat; Owner: postgres
--

COPY chat.blocklist (id, who, whom) FROM stdin;
2	2	1
\.


--
-- Data for Name: main; Type: TABLE DATA; Schema: chat; Owner: postgres
--

COPY chat.main (id, login, password, nickname) FROM stdin;
1	mamonov	b84342a09b8abc554e2b357cd6cc9dc1	mamonov
2	egorova	b84342a09b8abc554e2b357cd6cc9dc1	egorova
3	belova	b84342a09b8abc554e2b357cd6cc9dc1	belova
\.


--
-- Name: blocklist_id_seq; Type: SEQUENCE SET; Schema: chat; Owner: postgres
--

SELECT pg_catalog.setval('chat.blocklist_id_seq', 2, true);


--
-- Name: main_id_seq; Type: SEQUENCE SET; Schema: chat; Owner: postgres
--

SELECT pg_catalog.setval('chat.main_id_seq', 1, false);


--
-- Name: blocklist blocklist_pkey; Type: CONSTRAINT; Schema: chat; Owner: postgres
--

ALTER TABLE ONLY chat.blocklist
    ADD CONSTRAINT blocklist_pkey PRIMARY KEY (id);


--
-- Name: main main_pkey; Type: CONSTRAINT; Schema: chat; Owner: postgres
--

ALTER TABLE ONLY chat.main
    ADD CONSTRAINT main_pkey PRIMARY KEY (id);


--
-- Name: blocklist who; Type: FK CONSTRAINT; Schema: chat; Owner: postgres
--

ALTER TABLE ONLY chat.blocklist
    ADD CONSTRAINT who FOREIGN KEY (who) REFERENCES chat.main(id);


--
-- Name: blocklist whom; Type: FK CONSTRAINT; Schema: chat; Owner: postgres
--

ALTER TABLE ONLY chat.blocklist
    ADD CONSTRAINT whom FOREIGN KEY (whom) REFERENCES chat.main(id);


--
-- PostgreSQL database dump complete
--


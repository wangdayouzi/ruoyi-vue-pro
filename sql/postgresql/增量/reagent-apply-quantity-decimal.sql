-- 试剂申请明细数量改为两位小数：需求数量、累计发货数量、本次发货数量
ALTER TABLE reagent_apply_item
    ALTER COLUMN requested_qty TYPE NUMERIC(14, 2) USING requested_qty::NUMERIC(14, 2),
    ALTER COLUMN shipped_qty_total TYPE NUMERIC(14, 2) USING shipped_qty_total::NUMERIC(14, 2);

ALTER TABLE reagent_shipment_item
    ALTER COLUMN quantity_shipped TYPE NUMERIC(14, 2) USING quantity_shipped::NUMERIC(14, 2);

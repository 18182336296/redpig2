package com.redpigmall.manage.admin.tools;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.Inventory;
import com.redpigmall.domain.InventoryOperation;
import com.redpigmall.domain.StoreHouse;
import com.redpigmall.service.RedPigGoodsService;
import com.redpigmall.service.RedPigGoodsSpecPropertyService;
import com.redpigmall.service.RedPigInventoryOperationService;
import com.redpigmall.service.RedPigInventoryService;
import com.redpigmall.service.RedPigStoreHouseService;
@Component
@Transactional
public class RedPigInventoryTools {
	@Autowired
	private RedPigInventoryService inventoryService;
	@Autowired
	private RedPigStoreHouseService storeHouseService;
	@Autowired
	private RedPigGoodsService goodsService;
	@Autowired
	private RedPigInventoryOperationService inventoryOperationService;
	@Autowired
	private RedPigGoodsSpecPropertyService specPropertyService;

	@Async
	@Transactional
	public void async_updateInventory(Long storehouse_id, String storehouse_name) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("storehouse_id", storehouse_id);
		List<Inventory> inventory_list = this.inventoryService.queryPageList(params);
		
		for (Inventory obj : inventory_list) {
			obj.setStorehouse_name(storehouse_name);
			this.inventoryService.updateById(obj);
		}
	}

	@Async
	@Transactional
	public void async_updateInventoryByUpdateGoods(String update_inventory,
			String storehouse_id, String goods_id, String inventory_type,
			String intentory_details) {
		if ((update_inventory != null) && (!update_inventory.equals(""))
				&& (storehouse_id != null) && (!storehouse_id.equals(""))) {
			StoreHouse storeHouse = this.storeHouseService
					.selectByPrimaryKey(CommUtil.null2Long(storehouse_id));

			Goods goods = this.goodsService.selectByPrimaryKey(CommUtil
					.null2Long(goods_id));
			if ((goods.getGoods_type() == 0)
					&& (goods.getGoods_choice_type() == 0)) {
				Map<String, Object> params = Maps.newHashMap();
				params.put("goods_id", goods.getId());
				params.put("storehouse_id", CommUtil.null2Long(storehouse_id));
				List<Inventory> list = this.inventoryService
						.queryPageList(params);

				InventoryOperation inventoryOperation;
				if (inventory_type.equals("all")) {
					goods.setGoods_inventory_detail(null);
					
					int count = goods.getGoods_inventory();
					if (CommUtil.null2Int(Integer.valueOf(count)) > 0) {
						Inventory inventory;
						if (list.size() > 0) {
							inventory = (Inventory) list.get(0);
							inventory.setCount(count);
							this.inventoryService.updateById(inventory);
						} else {
							inventory = new Inventory();
							inventory.setAddTime(new Date());
							inventory.setStorehouse_id(CommUtil
									.null2Long(storehouse_id));
							inventory.setStorehouse_name(storeHouse
									.getSh_name());
							inventory.setGoods_id(goods.getId());
							inventory.setGoods_name(goods.getGoods_name());
							inventory.setSpec_id("");
							inventory.setSpec_name("无");
							inventory.setPrice(goods.getGoods_current_price());
							inventory.setCount(CommUtil.null2Int(Integer
									.valueOf(count)));
							this.inventoryService.saveEntity(inventory);
						}
						inventoryOperation = new InventoryOperation();
						inventoryOperation.setAddTime(new Date());
						inventoryOperation.setCount(CommUtil.null2Int(Integer
								.valueOf(count)));
						inventoryOperation.setGoods_name(inventory
								.getGoods_name());
						inventoryOperation.setStorehouse_name(inventory
								.getStorehouse_name());
						inventoryOperation.setInventory_id(inventory.getId());
						inventoryOperation.setSpec_name(inventory
								.getSpec_name());
						inventoryOperation.setType(1);
						inventoryOperation.setOperation_info("手动更新库存");
						this.inventoryOperationService
								.saveEntity(inventoryOperation);
					}
				} else if ((intentory_details != null)
						&& (!intentory_details.equals(""))) {
					String[] inventory_list = intentory_details.split(";");
					for (String inventory_str : inventory_list) {
						if (!inventory_str.equals("")) {
							String[] arr = inventory_str.split(",");
							Map<String, Object> map = Maps.newHashMap();
							map.put("id", arr[0]);
							map.put("count", arr[1]);
							map.put("price", arr[2]);

							int count = CommUtil.null2Int(arr[1]);
							String spec = arr[0];
							String[] sp = spec.split("_");
							Arrays.sort(sp);
							spec = "";
							for (String s : sp) {

								spec = spec + s + ",";
							}
							if (spec.length() > 0) {
								spec = spec.substring(0, spec.length() - 1);
							}
							params.put("spec_id", spec);
							params.put("storehouse_id",
									CommUtil.null2Long(storehouse_id));
							list = this.inventoryService.queryPageList(params);

							Inventory inventory;
							if (list.size() > 0) {
								inventory = (Inventory) list.get(0);
								inventory.setCount(count);
								this.inventoryService.updateById(inventory);
							} else {
								inventory = new Inventory();
								inventory.setAddTime(new Date());
								inventory.setStorehouse_id(CommUtil
										.null2Long(storehouse_id));
								inventory.setStorehouse_name(storeHouse
										.getSh_name());
								inventory.setGoods_id(goods.getId());
								inventory.setGoods_name(goods.getGoods_name());

								spec = map.get("id").toString();
								sp = spec.split("_");
								Arrays.sort(sp);
								spec = "";
								String specname = "";
								for (String s : sp) {

									spec = spec + s + ",";
									specname = specname
											+ " "
											+ this.specPropertyService
													.selectByPrimaryKey(
															CommUtil.null2Long(s))
													.getValue();
								}
								if (spec.length() > 0) {
									spec = spec.substring(0, spec.length() - 1);
								}
								inventory.setSpec_id(spec);
								inventory.setSpec_name(specname);
								inventory.setPrice(goods
										.getGoods_current_price());
								inventory.setCount(CommUtil.null2Int(Integer
										.valueOf(count)));
								this.inventoryService.saveEntity(inventory);
							}
							inventoryOperation = new InventoryOperation();
							inventoryOperation.setAddTime(new Date());
							inventoryOperation.setCount(CommUtil
									.null2Int(Integer.valueOf(count)));
							inventoryOperation.setGoods_name(inventory
									.getGoods_name());
							inventoryOperation.setStorehouse_name(inventory
									.getStorehouse_name());
							inventoryOperation.setInventory_id(inventory
									.getId());
							inventoryOperation.setSpec_name(inventory
									.getSpec_name());
							inventoryOperation.setType(1);
							inventoryOperation.setOperation_info("手动更新库存");
							this.inventoryOperationService
									.saveEntity(inventoryOperation);
						}
					}
				}
			}
		}
	}
}

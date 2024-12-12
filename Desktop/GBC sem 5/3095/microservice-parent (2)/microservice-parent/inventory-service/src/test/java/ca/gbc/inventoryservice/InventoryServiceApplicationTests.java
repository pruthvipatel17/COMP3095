package ca.gbc.inventoryservice;

import ca.gbc.inventoryservice.controller.InventoryController;
import ca.gbc.inventoryservice.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryController.class)
class InventoryServiceApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private InventoryService inventoryService;

	@Test
	void isInStock_ReturnsTrue() throws Exception {
		String skuCode = "SKU001";
		int quantity = 5;
		Mockito.when(inventoryService.isInStock(skuCode, quantity)).thenReturn(true);

		mockMvc.perform(get("/api/inventory")
						.param("skuCode", skuCode)
						.param("quantity", String.valueOf(quantity))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string("true"));
	}

	@Test
	void isInStock_ReturnsFalse() throws Exception {
		String skuCode = "SKU002";
		int quantity = 10;
		Mockito.when(inventoryService.isInStock(skuCode, quantity)).thenReturn(false);

		mockMvc.perform(get("/api/inventory")
						.param("skuCode", skuCode)
						.param("quantity", String.valueOf(quantity))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string("false"));
	}

	@Test
	void isInStock_MissingParams() throws Exception {
		mockMvc.perform(get("/api/inventory")
						.param("skuCode", "SKU003")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}
}

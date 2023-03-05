@SpringBootTest
@AutoConfigureMockMvc
public class PdfControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PdfService pdfService;

    @Test
    public void testGeneratePdf() throws Exception {
        // prepare input data
        Invoice invoice = new Invoice();
        invoice.setSeller("ABC Pvt. Ltd.");
        invoice.setSellerGstin("29AABBCCDD121ZD");
        invoice.setSellerAddress("New Delhi, India");
        invoice.setBuyer("Vedant Computers");
        invoice.setBuyerGstin("29AABBCCDD131ZD");
        invoice.setBuyerAddress("New Delhi, India");

        List<InvoiceItem> items = new ArrayList<>();
        InvoiceItem item1 = new InvoiceItem();
        item1.setName("Product 1");
        item1.setQuantity("12 Nos");
        item1.setRate(new BigDecimal("123.00"));
        item1.setAmount(new BigDecimal("1476.00"));
        items.add(item1);
        invoice.setItems(items);

        // prepare expected output data
        byte[] expectedPdfBytes = "test pdf bytes".getBytes();

        // configure mock behavior
        given(pdfService.generatePdf(eq(invoice))).willReturn(expectedPdfBytes);

        // perform API request
        MvcResult result = mockMvc.perform(
                post("/pdf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invoice)))
                .andExpect(status().isOk())
                .andReturn();

        // verify API response
        byte[] actualPdfBytes = result.getResponse().getContentAsByteArray();
        assertArrayEquals(expectedPdfBytes, actualPdfBytes);
    }

    private String asJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        return objectMapper.writeValueAsString(obj);
    }
}

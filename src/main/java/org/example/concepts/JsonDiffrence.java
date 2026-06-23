package org.example.concepts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;

import java.util.Map;

public class JsonDiffrence {

    static String json1 = "{\"id\":101,\"name\":\"Phanindra\",\"age\":28}";
    static String json2 = "{\"iqd\":101,\"name\":\"Phanindra\",\"age\":22}";
    static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        method1_ManualMapDiff();
        method2_JsonNodeDiff();
        method3_ZJsonPatch();
        method4_JSONAssertStrict();
        method5_JSONAssertNonStrict();
        method6_JSONCompareResult();
        method7_StringEquality();
    }

    // ── 1. Manual diff using Map ──────────────────────────────────────────────
    // Simplest approach — convert to Map and compare keys/values
    static void method1_ManualMapDiff() throws JsonProcessingException {
        System.out.println("\n── Method 1: Manual Map Diff ──");
        Map<String, Object> map1 = mapper.readValue(json1, Map.class);
        Map<String, Object> map2 = mapper.readValue(json2, Map.class);

        for (String key : map1.keySet()) {
            if (!map2.containsKey(key))
                System.out.println(key + " : only in json1 = " + map1.get(key));
            else if (!map1.get(key).equals(map2.get(key)))
                System.out.println(key + " : json1=" + map1.get(key) + " | json2=" + map2.get(key));
        }
        for (String key : map2.keySet()) {
            if (!map1.containsKey(key))
                System.out.println(key + " : only in json2 = " + map2.get(key));
        }
    }

    // ── 2. JsonNode diff ─────────────────────────────────────────────────────
    // Uses Jackson JsonNode — good for nested JSON traversal
    static void method2_JsonNodeDiff() throws JsonProcessingException {
        System.out.println("\n── Method 2: JsonNode Diff ──");
        JsonNode node1 = mapper.readTree(json1);
        JsonNode node2 = mapper.readTree(json2);

        node1.fields().forEachRemaining(entry -> {
            String key = entry.getKey();
            if (!node2.has(key))
                System.out.println(key + " : only in json1 = " + entry.getValue());
            else if (!node2.get(key).equals(entry.getValue()))
                System.out.println(key + " : json1=" + entry.getValue() + " | json2=" + node2.get(key));
        });

        node2.fieldNames().forEachRemaining(key -> {
            if (!node1.has(key))
                System.out.println(key + " : only in json2 = " + node2.get(key));
        });
    }

    // ── 3. zjsonpatch — RFC 6902 structured patch ────────────────────────────
    // Returns a JSON patch array: [{op, path, value}] — industry standard format
    static void method3_ZJsonPatch() throws JsonProcessingException {
        System.out.println("\n── Method 3: zjsonpatch (RFC 6902) ──");
        JsonNode node1 = mapper.readTree(json1);
        JsonNode node2 = mapper.readTree(json2);

        JsonNode patch = JsonDiff.asJson(node1, node2);
        // each entry has: op (add/remove/replace), path, value
        patch.forEach(p -> System.out.println(
                "op=" + p.get("op") + " | path=" + p.get("path") + " | value=" + p.get("value")
        ));
    }

    // ── 4. JSONAssert — strict mode ──────────────────────────────────────────
    // Strict: field order + extra fields matter — throws AssertionError if different
    static void method4_JSONAssertStrict() {
        System.out.println("\n── Method 4: JSONAssert Strict ──");
        try {
            JSONAssert.assertEquals(json1, json2, true);
            System.out.println("JSONs are equal");
        } catch (Exception e) {
            System.out.println("Diff: " + e.getMessage());
        }
    }

    // ── 5. JSONAssert — non-strict mode ─────────────────────────────────────
    // Non-strict: ignores extra fields in actual JSON
    static void method5_JSONAssertNonStrict() {
        System.out.println("\n── Method 5: JSONAssert Non-Strict ──");
        try {
            JSONAssert.assertEquals(json1, json2, false);
            System.out.println("JSONs are equal");
        } catch (Exception e) {
            System.out.println("Diff: " + e.getMessage());
        }
    }

    // ── 6. JSONCompareResult — get diff details without throwing ─────────────
    // Best for audit logging — gives you pass/fail + field-level diff info
    static void method6_JSONCompareResult() throws Exception {
        System.out.println("\n── Method 6: JSONCompareResult ──");
        JSONCompareResult result = JSONCompare.compareJSON(json1, json2, JSONCompareMode.LENIENT);

        if (result.failed()) {
            System.out.println("Failed: " + result.getMessage());
            result.getFieldFailures().forEach(f ->
                    System.out.println("Field: " + f.getField() + " | expected=" + f.getExpected() + " | actual=" + f.getActual())
            );
            result.getFieldMissing().forEach(f ->
                    System.out.println("Missing in actual: " + f.getField())
            );
            result.getFieldUnexpected().forEach(f ->
                    System.out.println("Unexpected in actual: " + f.getField())
            );
        } else {
            System.out.println("JSONs are equal");
        }
    }

    // ── 7. Simple string equality ────────────────────────────────────────────
    // Fastest — but fails if key order differs even if content is same
    static void method7_StringEquality() throws JsonProcessingException {
        System.out.println("\n── Method 7: Normalized String Equality ──");
        // normalize by re-serializing (sorts nothing, but removes whitespace differences)
        JsonNode node1 = mapper.readTree(json1);
        JsonNode node2 = mapper.readTree(json2);
        boolean equal = mapper.writeValueAsString(node1).equals(mapper.writeValueAsString(node2));
        System.out.println("Are equal: " + equal);
    }
}

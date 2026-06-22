package com.sladamos.common.converter;

import com.sladamos.common.exception.DuplicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class StringDuplicator {

    private static final Pattern COUNTER_SUFFIX = Pattern.compile(" \\((\\d+)\\)$");

    public String duplicateStringWithCounter(String toDuplicate, List<String> existingStrings) throws DuplicationException {
        validateDuplicateStringWithCounterInput(toDuplicate, existingStrings);
        String base = extractBase(toDuplicate);
        long nextNumber = findNextNumber(base, existingStrings);
        return String.format("%s (%d)", base, nextNumber);
    }

    private void validateDuplicateStringWithCounterInput(String toDuplicate, List<String> existingStrings) throws DuplicationException {
        if (toDuplicate == null) {
            throw new DuplicationException("String to duplicate cannot be null");
        }
        if (existingStrings == null) {
            throw new DuplicationException("Existing strings cannot be null");
        }
        if (!existingStrings.contains(toDuplicate)) {
            throw new DuplicationException("String '" + toDuplicate + "' does not exist in the list of existing strings");
        }
    }

    private String extractBase(String title) {
        return COUNTER_SUFFIX.matcher(title).replaceAll("");
    }

    private long findNextNumber(String base, List<String> existingStrings) {
        long highestExistingNumber = existingStrings.stream().mapToLong(str -> {
            if (str.equals(base)) return 0;
            if (extractBase(str).equals(base)) return extractCounter(str);
            return -1;
        }).max().orElse(-1);
        return highestExistingNumber + 1;
    }

    private long extractCounter(String title) {
        Matcher matcher = COUNTER_SUFFIX.matcher(title);
        return matcher.find() ? Long.parseLong(matcher.group(1)) : 0;
    }
}

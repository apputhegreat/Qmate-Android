package com.quotemate.qmate.util;

import com.quotemate.qmate.model.Quote;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by anji kinnara on 7/4/17.
 */

public class FilterQuotes {
    public static ArrayList<Quote> getFilteredQuotes(String authorId, String tag) {
        int sixe = QuotesUtil.quotes.size();
        ArrayList<Quote> filteredQuotes = new ArrayList<>();
        if (authorId == null || Objects.equals(authorId, "-1")) {
            if (tag != null && !Objects.equals(tag,"All")) {
                for (Quote quote : QuotesUtil.quotes
                        ) {
                    for (String  mood : quote.tags
                            ) {
                        if (Objects.equals(mood.trim(), tag.trim())) {
                            filteredQuotes.add(quote);
                            break;
                        }
                    }
                }
            } else {
                filteredQuotes = QuotesUtil.quotes;
            }
        } else {
            for (Quote quote : QuotesUtil.quotes
                    ) {
                if (Objects.equals(quote.authorId, authorId)) {
                    if (tag != null && !Objects.equals(tag,"All")) {
                        for (String mood : quote.tags
                                ) {
                            if (Objects.equals(mood.trim(), tag.trim())) {
                                filteredQuotes.add(quote);
                                break;
                            }
                        }
                    } else {
                        filteredQuotes.add(quote);
                    }
                }
            }
        }
        return filteredQuotes;
    }
}

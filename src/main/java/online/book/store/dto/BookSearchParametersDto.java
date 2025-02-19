package online.book.store.dto;

public record BookSearchParametersDto(String[] titles,
                                      String[] authors,
                                      String[] isbns,
                                      String[] prices,
                                      String[] descriptions,
                                      String[] coverImages) {
}

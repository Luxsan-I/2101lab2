#!/bin/bash

# This script provides a very rough estimate of test coverage by analyzing test files

echo "========================================="
echo "Simple Coverage Analysis"
echo "========================================="

# Count source files and test files
SOURCE_FILES=$(find src/main/java -name "*.java" | wc -l)
TEST_FILES=$(find src/test/java -name "*.java" | wc -l)

echo "Source files: $SOURCE_FILES"
echo "Test files: $TEST_FILES"

# Count test methods directly
echo ""
echo "Test method counts:"
TOTAL_TESTS=0

# BookTest.java
if [ -f "src/test/java/com/university/bookstore/model/BookTest.java" ]; then
    BOOK_TESTS=$(grep "@Test" src/test/java/com/university/bookstore/model/BookTest.java | grep -v "@TestMethodOrder" | wc -l)
    echo "  BookTest.java: $BOOK_TESTS tests"
    TOTAL_TESTS=$((TOTAL_TESTS + BOOK_TESTS))
fi

# BookstoreArrayListTest.java
if [ -f "src/test/java/com/university/bookstore/impl/BookstoreArrayListTest.java" ]; then
    BOOKSTORE_TESTS=$(grep "@Test" src/test/java/com/university/bookstore/impl/BookstoreArrayListTest.java | wc -l)
    echo "  BookstoreArrayListTest.java: $BOOKSTORE_TESTS tests"
    TOTAL_TESTS=$((TOTAL_TESTS + BOOKSTORE_TESTS))
fi

# BookArrayUtilsTest.java
if [ -f "src/test/java/com/university/bookstore/utils/BookArrayUtilsTest.java" ]; then
    UTILS_TESTS=$(grep "@Test" src/test/java/com/university/bookstore/utils/BookArrayUtilsTest.java | wc -l)
    echo "  BookArrayUtilsTest.java: $UTILS_TESTS tests"
    TOTAL_TESTS=$((TOTAL_TESTS + UTILS_TESTS))
fi

echo "Total test methods: $TOTAL_TESTS"

# Count source methods directly
echo ""
echo "Source method counts:"
TOTAL_SOURCE_METHODS=0

# Book.java
if [ -f "src/main/java/com/university/bookstore/model/Book.java" ]; then
    BOOK_METHODS=$(grep -c "public.*(" src/main/java/com/university/bookstore/model/Book.java)
    echo "  Book.java: $BOOK_METHODS methods"
    TOTAL_SOURCE_METHODS=$((TOTAL_SOURCE_METHODS + BOOK_METHODS))
fi

# BookstoreArrayList.java
if [ -f "src/main/java/com/university/bookstore/impl/BookstoreArrayList.java" ]; then
    BOOKSTORE_METHODS=$(grep -c "public.*(" src/main/java/com/university/bookstore/impl/BookstoreArrayList.java)
    echo "  BookstoreArrayList.java: $BOOKSTORE_METHODS methods"
    TOTAL_SOURCE_METHODS=$((TOTAL_SOURCE_METHODS + BOOKSTORE_METHODS))
fi

# BookArrayUtils.java
if [ -f "src/main/java/com/university/bookstore/utils/BookArrayUtils.java" ]; then
    UTILS_METHODS=$(grep -c "public.*(" src/main/java/com/university/bookstore/utils/BookArrayUtils.java)
    echo "  BookArrayUtils.java: $UTILS_METHODS methods"
    TOTAL_SOURCE_METHODS=$((TOTAL_SOURCE_METHODS + UTILS_METHODS))
fi

echo "Total source methods: $TOTAL_SOURCE_METHODS"

# Estimate coverage based on test density
echo ""
if [ $TOTAL_SOURCE_METHODS -gt 0 ]; then
    COVERAGE_ESTIMATE=$((TOTAL_TESTS * 100 / TOTAL_SOURCE_METHODS))
    echo "Estimated coverage: ~${COVERAGE_ESTIMATE}%"
    
    if [ $COVERAGE_ESTIMATE -ge 90 ]; then
        echo "Great! Coverage target met (>= 90%)"
    elif [ $COVERAGE_ESTIMATE -ge 80 ]; then
        echo "Good coverage (80-89%)"
    elif [ $COVERAGE_ESTIMATE -ge 70 ]; then
        echo "Fair coverage (70-79%)"
    else
        echo "Low coverage (< 70%)"
    fi
fi

# Alternative: Use test file ratio
if [ $SOURCE_FILES -gt 0 ]; then
    TEST_RATIO=$((TEST_FILES * 100 / SOURCE_FILES))
    echo "Test file ratio: ${TEST_RATIO}% (tests per source file)"
fi

echo ""
echo "========================================="
echo "Note: This is a rough estimate."
echo "For accurate coverage, use: mvn jacoco:report"
echo "========================================="

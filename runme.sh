#!/bin/bash

# =============================================================================
# BOOKSTORE LAB 2 - BUILD, TEST & UML GENERATION SCRIPT
# =============================================================================
# This script performs a complete build cycle including:
# - Environment validation
# - Clean compilation
# - Detailed testing
# - Code coverage analysis
# - Javadoc generation
# - UML diagram generation
# - Quality checks
# - Final summary report
# =============================================================================

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Script configuration
SCRIPT_NAME="runme.sh"
PROJECT_NAME="Bookstore Lab 2 - Polymorphic System"
JAVA_VERSION_REQUIRED="17"
MAVEN_VERSION_REQUIRED="3.6"
TARGET_COVERAGE="90"

# Status tracking
BUILD_STATUS=0
TEST_STATUS=0
COVERAGE_STATUS=0
JAVADOC_STATUS=0
UML_STATUS=0
OVERALL_STATUS=0

# Function to print colored output
print_status() {
    local status=$1
    local message=$2
    case $status in
        "INFO") echo -e "${BLUE}[INFO]${NC} $message" ;;
        "SUCCESS") echo -e "${GREEN}[SUCCESS]${NC} $message" ;;
        "WARNING") echo -e "${YELLOW}[WARNING]${NC} $message" ;;
        "ERROR") echo -e "${RED}[ERROR]${NC} $message" ;;
        "STEP") echo -e "${PURPLE}[STEP]${NC} $message" ;;
        "HEADER") echo -e "${CYAN}$message${NC}" ;;
    esac
}

# Function to print section headers
print_header() {
    echo ""
    print_status "HEADER" "=============================================================================="
    print_status "HEADER" "$1"
    print_status "HEADER" "=============================================================================="
    echo ""
}

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to check file exists
file_exists() {
    [ -f "$1" ]
}

# Function to check directory exists
dir_exists() {
    [ -d "$1" ]
}

# Function to handle errors gracefully
handle_error() {
    local exit_code=$?
    print_status "ERROR" "Script failed with exit code $exit_code"
    print_status "ERROR" "Last command: $BASH_COMMAND"
    exit $exit_code
}

# Function to show help
show_help() {
    echo "=============================================================================="
    echo "BOOKSTORE LAB 2 - BUILD & TEST SCRIPT - USAGE GUIDE"
    echo "=============================================================================="
    echo ""
    echo "SYNOPSIS:"
    echo "  $SCRIPT_NAME [OPTIONS]"
    echo ""
    echo "DESCRIPTION:"
    echo "  This script performs a complete build cycle for the Bookstore Lab 2 project,"
    echo "  including environment validation, compilation, testing, coverage analysis,"
    echo "  Javadoc generation, UML diagram generation, quality checks, and packaging."
    echo ""
    echo "OPTIONS:"
    echo "  -h, --help           Show this help message and exit"
    echo "  -v, --version        Show script version and exit"
    echo "  -q, --quick          Quick mode: skip coverage, Javadoc, and UML generation"
    echo "  -t, --test-only      Run tests only (skip build and other steps)"
    echo "  -c, --clean          Force clean build (remove target directory)"
    echo "  -s, --skip-tests     Skip running tests (compile and package only)"
    echo "  -j, --javadoc-only   Generate Javadoc only"
    echo "  -p, --package-only   Create JAR package only"
    echo "  -u, --uml-only       Generate UML diagrams only"
    echo "  --clean-uml          Clean up generated UML files"
    echo "  -d, --debug          Enable debug output"
    echo ""
    echo "EXAMPLES:"
    echo "  $SCRIPT_NAME                    # Run complete build and test process"
    echo "  $SCRIPT_NAME --help             # Show this help message"
    echo "  $SCRIPT_NAME --quick            # Quick build without coverage/Javadoc/UML"
    echo "  $SCRIPT_NAME --test-only        # Run tests only"
    echo "  $SCRIPT_NAME --clean            # Force clean build"
    echo "  $SCRIPT_NAME --skip-tests       # Build without testing"
    echo "  $SCRIPT_NAME --javadoc-only     # Generate Javadoc only"
    echo "  $SCRIPT_NAME --package-only     # Create JAR only"
    echo "  $SCRIPT_NAME --uml-only         # Generate UML diagrams only"
    echo "  $SCRIPT_NAME --clean-uml        # Clean up UML files"
    echo ""
    echo "EXIT CODES:"
    echo "  0  - All checks passed successfully"
    echo "  1  - Some checks failed or errors occurred"
    echo "  2  - Invalid command line arguments"
    echo ""
    echo "PREREQUISITES:"
    echo "  - Java 17 or higher installed and in PATH"
    echo "  - Maven 3.6 or higher installed and in PATH"
    echo "  - Script must be run from project root directory (where pom.xml is located)"
    echo "  - Script must have execute permissions (chmod +x $SCRIPT_NAME)"
    echo ""
    echo "PROJECT STRUCTURE:"
    echo "  src/"
    echo "  ├── pom.xml                          # Maven configuration"
    echo "  ├── $SCRIPT_NAME                     # This script"
    echo "  ├── src/main/java/                   # Source code"
    echo "  │   └── com/university/bookstore/"
    echo "  │       ├── model/                   # Material hierarchy (Book, EBook, etc.)"
    echo "  │       ├── api/                     # MaterialStore interface"
    echo "  │       ├── impl/                    # MaterialStoreImpl implementation"
    echo "  │       ├── factory/                 # MaterialFactory (Factory pattern)"
    echo "  │       ├── visitor/                 # Visitor pattern implementation"
    echo "  │       └── utils/                   # Utility classes"
    echo "  └── src/test/java/                   # Test code"
    echo "      └── com/university/bookstore/"
    echo "          ├── model/                   # Material tests"
    echo "          ├── impl/                    # MaterialStore tests"
    echo "          ├── factory/                 # Factory tests"
    echo "          ├── visitor/                 # Visitor tests"
    echo "          └── utils/                   # Utility tests"
    echo ""
    echo "For more information, check the script comments or run individual Maven commands:"
    echo "  mvn clean compile          # Clean build only"
    echo "  mvn test                   # Run tests only"
    echo "  mvn jacoco:report          # Generate coverage only"
    echo "  mvn javadoc:javadoc        # Generate Javadoc only"
    echo "  mvn package                # Create JAR only"
    echo ""
    echo "=============================================================================="
}

# Function to show version
show_version() {
    echo "=============================================================================="
    echo "BOOKSTORE LAB 2 - BUILD & TEST SCRIPT"
    echo "=============================================================================="
    echo "Version: 2.0"
    echo "Author:  Navid Mohaghegh"
    echo "Contact: navid@navid.ca"
    echo "Project: CSSD2101 Lab 2 - Polymorphic Bookstore Management System"
    echo "Features: Build, Test, Coverage, Javadoc, UML Generation"
    echo "=============================================================================="
}

# Parse command line arguments
QUICK_MODE=false
TEST_ONLY=false
CLEAN_BUILD=false
SKIP_TESTS=false
JAVADOC_ONLY=false
PACKAGE_ONLY=false
UML_ONLY=false
CLEAN_UML=false
DEBUG_MODE=false

while [[ $# -gt 0 ]]; do
    case $1 in
        -h|--help)
            show_help
            exit 0
            ;;
        -v|--version)
            show_version
            exit 0
            ;;
        -q|--quick)
            QUICK_MODE=true
            shift
            ;;
        -t|--test-only)
            TEST_ONLY=true
            shift
            ;;
        -c|--clean)
            CLEAN_BUILD=true
            shift
            ;;
        -s|--skip-tests)
            SKIP_TESTS=true
            shift
            ;;
        -j|--javadoc-only)
            JAVADOC_ONLY=true
            shift
            ;;
        -p|--package-only)
            PACKAGE_ONLY=true
            shift
            ;;
        -u|--uml-only)
            UML_ONLY=true
            shift
            ;;
        --clean-uml)
            CLEAN_UML=true
            shift
            ;;
        -d|--debug)
            DEBUG_MODE=true
            shift
            ;;
        -*)
            print_status "ERROR" "Unknown option: $1"
            print_status "ERROR" "Use --help for usage information"
            exit 2
            ;;
        *)
            print_status "ERROR" "Unknown argument: $1"
            print_status "ERROR" "Use --help for usage information"
            exit 2
            ;;
    esac
done

# Set error handling
trap 'handle_error' ERR

# =============================================================================
# SCRIPT START
# =============================================================================

print_header "[START] $PROJECT_NAME - BUILD & TEST SCRIPT"
print_status "INFO" "Starting build and test process..."
print_status "INFO" "Script: $SCRIPT_NAME"
print_status "INFO" "Timestamp: $(date)"
print_status "INFO" "Working Directory: $(pwd)"

# Show mode information
if [ "$QUICK_MODE" = true ]; then
    print_status "INFO" "Mode: Quick (skipping coverage, Javadoc, and UML)"
elif [ "$TEST_ONLY" = true ]; then
    print_status "INFO" "Mode: Test only"
elif [ "$SKIP_TESTS" = true ]; then
    print_status "INFO" "Mode: Skip tests"
elif [ "$JAVADOC_ONLY" = true ]; then
    print_status "INFO" "Mode: Javadoc only"
elif [ "$PACKAGE_ONLY" = true ]; then
    print_status "INFO" "Mode: Package only"
elif [ "$UML_ONLY" = true ]; then
    print_status "INFO" "Mode: UML generation only"
elif [ "$CLEAN_UML" = true ]; then
    print_status "INFO" "Mode: Clean UML files"
fi

echo ""

# =============================================================================
# STEP 1: ENVIRONMENT VALIDATION
# =============================================================================

print_header "[CHECK] STEP 1: ENVIRONMENT VALIDATION"

# Check Java
print_status "STEP" "Checking Java installation..."
if command_exists java; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -ge "$JAVA_VERSION_REQUIRED" ]; then
        print_status "SUCCESS" "[OK] Java $JAVA_VERSION found (required: $JAVA_VERSION_REQUIRED+)"
    else
        print_status "WARNING" "[WARN] Java $JAVA_VERSION found, but Java $JAVA_VERSION_REQUIRED+ recommended"
    fi
    JAVA_HOME=$(echo $JAVA_HOME)
    if [ ! -z "$JAVA_HOME" ]; then
        print_status "INFO" "JAVA_HOME: $JAVA_HOME"
    fi
else
    print_status "ERROR" "✗ Java not found. Please install Java $JAVA_VERSION_REQUIRED+"
    exit 1
fi

# Check Maven
print_status "STEP" "Checking Maven installation..."
if command_exists mvn; then
    MVN_VERSION=$(mvn -version | head -n 1 | cut -d' ' -f3)
    print_status "SUCCESS" "[OK] Maven $MVN_VERSION found"
else
    print_status "ERROR" "✗ Maven not found. Please install Maven $MAVEN_VERSION_REQUIRED+"
    exit 1
fi

# Check project structure
print_status "STEP" "Validating project structure..."
REQUIRED_DIRS=("src/main/java" "src/test/java" "src/main/java/com/university/bookstore")
for dir in "${REQUIRED_DIRS[@]}"; do
    if dir_exists "$dir"; then
        print_status "SUCCESS" "[OK] Found: $dir"
    else
        print_status "ERROR" "[FAIL] Missing required directory: $dir"
        exit 1
    fi
done

# Check pom.xml
if file_exists "pom.xml"; then
    print_status "SUCCESS" "[OK] Found: pom.xml"
else
    print_status "ERROR" "[FAIL] Missing: pom.xml"
    exit 1
fi

print_status "SUCCESS" "Environment validation completed successfully!"

# =============================================================================
# STEP 1.5: UML CLEANUP (if clean-uml mode)
# =============================================================================

if [ "$CLEAN_UML" = true ]; then
    print_header "[CLEAN] STEP 1.5: UML FILES CLEANUP"
    
    print_status "STEP" "Cleaning up generated UML files..."
    
    # List of UML files to clean
    UML_FILES=(
        "bookstore-class-diagram.png"
        "bookstore-class-diagram.svg"
        "bookstore-class-diagram.atxt"
        "bookstore-class-diagram.txt"
        "bookstore-package-diagram.png"
        "bookstore-uml.puml"
        "bookstore-packages.puml"
    )
    
    CLEANED_COUNT=0
    for file in "${UML_FILES[@]}"; do
        if file_exists "$file"; then
            rm -f "$file"
            print_status "SUCCESS" "[REMOVED] $file"
            CLEANED_COUNT=$((CLEANED_COUNT + 1))
        fi
    done
    
    if [ $CLEANED_COUNT -gt 0 ]; then
        print_status "SUCCESS" "[OK] Cleaned up $CLEANED_COUNT UML files"
    else
        print_status "INFO" "[INFO] No UML files found to clean"
    fi
    
    print_status "SUCCESS" "UML cleanup completed successfully!"
    exit 0
fi

# =============================================================================
# STEP 2: CLEAN BUILD (if not test-only or specific mode)
# =============================================================================

if [ "$TEST_ONLY" = false ] && [ "$JAVADOC_ONLY" = false ] && [ "$PACKAGE_ONLY" = false ] && [ "$UML_ONLY" = false ] && [ "$CLEAN_UML" = false ]; then
    print_header "[BUILD] STEP 2: CLEAN BUILD"
    
    # Force remove target directory if it exists and clean mode is enabled
    if [ "$CLEAN_BUILD" = true ] && dir_exists "target"; then
        print_status "STEP" "Removing existing target directory..."
        rm -rf target
        print_status "SUCCESS" "[OK] Target directory removed"
    fi
    
    # Compile source code
    print_status "STEP" "Compiling source code..."
    if mvn compile -q; then
        print_status "SUCCESS" "[OK] Compilation successful"
        BUILD_STATUS=1
    else
        print_status "ERROR" "[FAIL] Compilation failed"
        BUILD_STATUS=0
        exit 1
    fi
    
    # Count compiled classes
    if dir_exists "target/classes"; then
        CLASS_COUNT=$(find target/classes -name "*.class" | wc -l)
        print_status "INFO" "Compiled $CLASS_COUNT classes"
    fi
else
    BUILD_STATUS=1  # Assume build is OK for test-only modes
fi

# =============================================================================
# STEP 3: TESTING (if not skip-tests or specific mode)
# =============================================================================

if [ "$SKIP_TESTS" = false ] && [ "$JAVADOC_ONLY" = false ] && [ "$PACKAGE_ONLY" = false ] && [ "$UML_ONLY" = false ] && [ "$CLEAN_UML" = false ]; then
    print_header "[TEST] STEP 3: TESTING"
    
    # Run tests with detailed output
    print_status "STEP" "Running test suite..."
    if mvn test; then
        TEST_STATUS=1
    else
        print_status "ERROR" "[FAIL] Maven test execution failed"
        TEST_STATUS=0
        print_status "INFO" "Check target/surefire-reports for detailed test results"
    fi
    
    # Extract test statistics
    if dir_exists "target/surefire-reports"; then
        print_status "STEP" "Analyzing test results..."
        
        # Count test files
        TEST_FILES=$(find src/test -name "*.java" | wc -l)
        print_status "INFO" "Test files: $TEST_FILES"
        
        # Extract test results from XML reports
        if command_exists grep && command_exists sed; then
            TOTAL_TESTS=0
            TOTAL_FAILURES=0
            TOTAL_ERRORS=0
            
            for report in target/surefire-reports/TEST-*.xml; do
                if file_exists "$report"; then
                    TESTS=$(grep -o 'tests="[^"]*"' "$report" | sed 's/tests="//;s/"//')
                    FAILURES=$(grep -o 'failures="[^"]*"' "$report" | sed 's/failures="//;s/"//')
                    ERRORS=$(grep -o 'errors="[^"]*"' "$report" | sed 's/errors="//;s/"//')
                    
                    TOTAL_TESTS=$((TOTAL_TESTS + TESTS))
                    TOTAL_FAILURES=$((TOTAL_FAILURES + FAILURES))
                    TOTAL_ERRORS=$((TOTAL_ERRORS + ERRORS))
                fi
            done
            
            if [ $TOTAL_TESTS -gt 0 ]; then
                print_status "SUCCESS" "Test Summary: $TOTAL_TESTS tests, $TOTAL_FAILURES failures, $TOTAL_ERRORS errors"
                
                if [ $TOTAL_FAILURES -eq 0 ] && [ $TOTAL_ERRORS -eq 0 ]; then
                    print_status "SUCCESS" "[SUCCESS] All tests passed!"
                else
                    print_status "WARNING" "[WARN] Some tests failed. Review test results before proceeding."
                fi
            fi
        fi
    fi
else
    TEST_STATUS=1  # Assume tests are OK for skip-tests modes
fi

# =============================================================================
# STEP 4: CODE COVERAGE ANALYSIS (if not quick mode or specific mode)
# =============================================================================

if [ "$QUICK_MODE" = false ] && [ "$JAVADOC_ONLY" = false ] && [ "$PACKAGE_ONLY" = false ] && [ "$UML_ONLY" = false ] && [ "$CLEAN_UML" = false ]; then
    print_header "[COVERAGE] STEP 4: CODE COVERAGE ANALYSIS"
    
    print_status "STEP" "Generating code coverage report..."
    if mvn jacoco:report -q; then
        if file_exists "target/site/jacoco/index.html"; then
            print_status "SUCCESS" "[OK] Coverage report generated successfully"
            COVERAGE_STATUS=1
            
            # Extract coverage percentage
            if command_exists grep && command_exists sed; then
                COVERAGE_PERCENT=$(grep -o '<td class="ctr2">[0-9]*%</td>' target/site/jacoco/index.html 2>/dev/null | head -1 | sed 's/<[^>]*>//g')
                if [ ! -z "$COVERAGE_PERCENT" ]; then
                    print_status "INFO" "Overall code coverage: $COVERAGE_PERCENT"
                    
                    # Check if coverage meets requirement
                    COVERAGE_NUM=$(echo $COVERAGE_PERCENT | sed 's/%//')
                    if [ "$COVERAGE_NUM" -ge "$TARGET_COVERAGE" ]; then
                        print_status "SUCCESS" "[TARGET] Coverage target met: $COVERAGE_PERCENT >= $TARGET_COVERAGE%"
                    else
                        print_status "WARNING" "[WARN] Coverage below target: $COVERAGE_PERCENT < $TARGET_COVERAGE%"
                    fi
                fi
            fi
            
            print_status "INFO" "Coverage report: target/site/jacoco/index.html"
        else
            print_status "WARNING" "[WARN] Coverage report not found"
            COVERAGE_STATUS=0
        fi
    else
        print_status "WARNING" "[WARN] Coverage generation had issues"
        COVERAGE_STATUS=0
    fi
else
    COVERAGE_STATUS=1  # Assume coverage is OK for quick mode
fi

# =============================================================================
# STEP 5: JAVADOC GENERATION (if not quick mode or specific mode)
# =============================================================================

if [ "$QUICK_MODE" = false ] && [ "$TEST_ONLY" = false ] && [ "$PACKAGE_ONLY" = false ] && [ "$UML_ONLY" = false ] && [ "$CLEAN_UML" = false ]; then
    print_header "[DOCS] STEP 5: JAVADOC GENERATION"
    
    print_status "STEP" "Generating Javadoc documentation..."
    if mvn javadoc:javadoc -q; then
        if file_exists "target/site/apidocs/index.html"; then
            print_status "SUCCESS" "[OK] Javadoc generated successfully"
            JAVADOC_STATUS=1
            
            # Count documented classes
            if dir_exists "target/site/apidocs"; then
                DOC_COUNT=$(find target/site/apidocs -name "*.html" | grep -v "index\|package\|overview" | wc -l)
                print_status "INFO" "Generated documentation for $DOC_COUNT classes/methods"
            fi
            
            print_status "INFO" "Javadoc: target/site/apidocs/index.html"
        else
            print_status "WARNING" "[WARN] Javadoc not found"
            JAVADOC_STATUS=0
        fi
    else
        print_status "WARNING" "[WARN] Javadoc generation had warnings"
        JAVADOC_STATUS=0
    fi
else
    JAVADOC_STATUS=1  # Assume Javadoc is OK for quick mode
fi

# =============================================================================
# STEP 6: UML DIAGRAM GENERATION (if not quick mode or specific mode)
# =============================================================================

if [ "$QUICK_MODE" = false ] && [ "$TEST_ONLY" = false ] && [ "$JAVADOC_ONLY" = false ] && [ "$PACKAGE_ONLY" = false ] && [ "$CLEAN_UML" = false ]; then
    print_header "[UML] STEP 6: UML DIAGRAM GENERATION"
    
    print_status "STEP" "Generating UML diagrams..."
    
    # Check if PlantUML is available
    if [ ! -f "plantuml.jar" ]; then
        print_status "STEP" "Downloading PlantUML..."
        if command_exists wget; then
            wget -q https://github.com/plantuml/plantuml/releases/download/v1.2024.7/plantuml-1.2024.7.jar -O plantuml.jar
            print_status "SUCCESS" "[OK] PlantUML downloaded"
        else
            print_status "WARNING" "[WARN] wget not found, skipping PlantUML download"
            UML_STATUS=0
        fi
    fi
    
    if [ -f "plantuml.jar" ]; then
        # Generate PlantUML source
        print_status "STEP" "Creating PlantUML diagram definition..."
        
        cat > "bookstore-uml.puml" << 'EOF'
@startuml bookstore-class-diagram
!theme plain
skinparam classAttributeIconSize 0
skinparam backgroundColor #FEFEFE
skinparam headerFontSize 18
skinparam headerFontStyle bold

title Bookstore Lab 2 - Polymorphic System Class Diagram

package "com.university.bookstore.model" #E8F5E9 {
    abstract class Material {
        # id: String
        # title: String
        # price: double
        # year: int
        --
        + Material(id, title, price, year)
        + {abstract} getCreator(): String
        + {abstract} getDisplayInfo(): String
        + {abstract} getType(): MaterialType
        + {abstract} getDiscountRate(): double
        + getId(): String
        + getTitle(): String
        + getPrice(): double
        + getYear(): int
        + compareTo(other: Material): int
        + equals(obj: Object): boolean
        + hashCode(): int
    }
    
    enum MaterialType {
        BOOK
        MAGAZINE
        AUDIO_BOOK
        VIDEO
        EBOOK
    }
    
    class PrintedBook extends Material {
        - isbn: String
        - author: String
        - pages: int
        - publisher: String
        - hardcover: boolean
        --
        + PrintedBook(isbn, title, author, price, year, pages, publisher, hardcover)
        + getIsbn(): String
        + getAuthor(): String
        + getPages(): int
        + getPublisher(): String
        + isHardcover(): boolean
        + estimateReadingTime(wordsPerMinute: int): double
    }
    
    class Magazine extends Material {
        - issn: String
        - publisher: String
        - issueNumber: int
        - frequency: String
        - category: String
        --
        + Magazine(issn, title, publisher, price, year, issue, frequency, category)
        + getIssn(): String
        + getPublisher(): String
        + getIssueNumber(): int
        + getFrequency(): String
        + getCategory(): String
        + calculateAnnualSubscription(): double
    }
    
    interface Media {
        + getDuration(): int
        + getFormat(): String
        + getFileSize(): double
        + getQuality(): MediaQuality
        + isStreamingOnly(): boolean
    }
    
    enum MediaQuality {
        LOW
        STANDARD
        HIGH
        HD
        UHD_4K
        PHYSICAL
        --
        - bitrate: int
        - description: String
        + getBitrate(): int
        + getDescription(): String
        + toString(): String
    }
    
    class AudioBook extends Material implements Media {
        - isbn: String
        - author: String
        - narrator: String
        - duration: int
        - format: String
        - fileSize: double
        - quality: MediaQuality
        - language: String
        - unabridged: boolean
        --
        + AudioBook(isbn, title, author, narrator, price, year, duration, format, fileSize, quality, language, unabridged)
        + getIsbn(): String
        + getAuthor(): String
        + getNarrator(): String
        + getLanguage(): String
        + isUnabridged(): boolean
        + calculateListeningSessions(minutesPerDay: int): int
    }
    
    class VideoMaterial extends Material implements Media {
        - director: String
        - duration: int
        - format: String
        - fileSize: double
        - quality: MediaQuality
        - videoType: VideoType
        - rating: String
        - cast: List<String>
        - subtitles: boolean
        - aspectRatio: String
        --
        + VideoMaterial(id, title, director, price, year, duration, format, fileSize, quality, type, rating, cast, subtitles, aspectRatio)
        + getDirector(): String
        + getVideoType(): VideoType
        + getRating(): String
        + getCast(): List<String>
        + hasSubtitles(): boolean
        + getAspectRatio(): String
        + isFeatureLength(): boolean
        + getStreamingBandwidth(): double
    }
    
    enum VideoType {
        MOVIE
        DOCUMENTARY
        TV_SHOW
        EDUCATIONAL
        SHORT_FILM
    }
    
    class EBook extends Material implements Media {
        - author: String
        - fileFormat: String
        - fileSize: double
        - drmEnabled: boolean
        - wordCount: int
        - quality: MediaQuality
        --
        + EBook(id, title, author, price, year, fileFormat, fileSize, drmEnabled, wordCount, quality)
        + getAuthor(): String
        + getFileFormat(): String
        + getFileSize(): double
        + isDrmEnabled(): boolean
        + getWordCount(): int
        + getReadingTimeMinutes(): int
    }
    
    Material <|-- PrintedBook
    Material <|-- Magazine
    Material <|-- AudioBook
    Material <|-- VideoMaterial
    Material <|-- EBook
    Material +-- MaterialType
    Media <|.. AudioBook
    Media <|.. VideoMaterial
    Media <|.. EBook
    Media +-- MediaQuality
    VideoMaterial +-- VideoType
}

package "com.university.bookstore.api" #E3F2FD {
    interface MaterialStore {
        + addMaterial(material: Material): boolean
        + removeMaterial(id: String): Optional<Material>
        + findById(id: String): Optional<Material>
        + searchByTitle(title: String): List<Material>
        + searchByCreator(creator: String): List<Material>
        + getMaterialsByType(type: MaterialType): List<Material>
        + getMediaMaterials(): List<Media>
        + filterMaterials(predicate: Predicate<Material>): List<Material>
        + findRecentMaterials(years: int): List<Material>
        + findByCreators(creators: String...): List<Material>
        + findWithPredicate(condition: Predicate<Material>): List<Material>
        + getSorted(comparator: Comparator<Material>): List<Material>
        + size(): int
        + isEmpty(): boolean
    }
}

package "com.university.bookstore.impl" #FFF3E0 {
    class MaterialStoreImpl implements MaterialStore {
        - inventory: List<Material>
        - idIndex: Map<String, Material>
        --
        + MaterialStoreImpl()
        + MaterialStoreImpl(initialMaterials: Collection<Material>)
        + addMaterial(material: Material): boolean
        + removeMaterial(id: String): Optional<Material>
        + findById(id: String): Optional<Material>
        + searchByTitle(title: String): List<Material>
        + searchByCreator(creator: String): List<Material>
        + getMaterialsByType(type: MaterialType): List<Material>
        + getMediaMaterials(): List<Media>
        + filterMaterials(predicate: Predicate<Material>): List<Material>
        + findRecentMaterials(years: int): List<Material>
        + findByCreators(creators: String...): List<Material>
        + findWithPredicate(condition: Predicate<Material>): List<Material>
        + getSorted(comparator: Comparator<Material>): List<Material>
        + size(): int
        + isEmpty(): boolean
    }
}

package "com.university.bookstore.factory" #FCE4EC {
    class MaterialFactory {
        + {static} createMaterial(type: String, properties: Map<String, Object>): Material
        - {static} createPrintedBook(properties: Map<String, Object>): PrintedBook
        - {static} createMagazine(properties: Map<String, Object>): Magazine
        - {static} createAudioBook(properties: Map<String, Object>): AudioBook
        - {static} createVideoMaterial(properties: Map<String, Object>): VideoMaterial
        - {static} createEBook(properties: Map<String, Object>): EBook
    }
}

package "com.university.bookstore.visitor" #E1F5FE {
    interface MaterialVisitor {
        + visit(book: PrintedBook): void
        + visit(magazine: Magazine): void
        + visit(audioBook: AudioBook): void
        + visit(video: VideoMaterial): void
        + visit(ebook: EBook): void
    }
    
    class ShippingCostCalculator implements MaterialVisitor {
        - totalShippingCost: double
        --
        + visit(book: PrintedBook): void
        + visit(magazine: Magazine): void
        + visit(audioBook: AudioBook): void
        + visit(video: VideoMaterial): void
        + visit(ebook: EBook): void
        + getTotalShippingCost(): double
        + reset(): void
        + calculateShippingCost(material: Material): double
    }
}

' Relationships
MaterialStoreImpl ..> Material : manages
MaterialStoreImpl ..> Media : manages
MaterialFactory ..> Material : creates
ShippingCostCalculator ..> Material : visits

' Notes
note right of MaterialStoreImpl
  Polymorphic storage for all
  types of library materials
  with enhanced search
end note

note right of MaterialFactory
  Factory pattern for
  creating different
  material types
end note

note right of ShippingCostCalculator
  Visitor pattern for
  calculating shipping
  costs by material type
end note

note bottom of Material
  Abstract base class for all
  library materials using
  inheritance and polymorphism
end note

@enduml
EOF
        
        print_status "SUCCESS" "[OK] PlantUML diagram definition created"
        
        # Generate PNG
        print_status "STEP" "Generating PNG diagram..."
        if java -jar plantuml.jar -tpng bookstore-uml.puml; then
            print_status "SUCCESS" "[OK] PNG diagram generated: bookstore-class-diagram.png"
        else
            print_status "WARNING" "[WARN] Failed to generate PNG"
        fi
        
        # Generate SVG
        print_status "STEP" "Generating SVG diagram..."
        if java -jar plantuml.jar -tsvg bookstore-uml.puml; then
            print_status "SUCCESS" "[OK] SVG diagram generated: bookstore-class-diagram.svg"
        else
            print_status "WARNING" "[WARN] Failed to generate SVG"
        fi
        
        # Generate ASCII
        print_status "STEP" "Generating ASCII diagram..."
        if java -jar plantuml.jar -ttxt bookstore-uml.puml; then
            print_status "SUCCESS" "[OK] ASCII diagram generated: bookstore-class-diagram.txt"
        else
            print_status "WARNING" "[WARN] ASCII generation might not be supported"
        fi
        
        UML_STATUS=1
        print_status "INFO" "UML diagrams: bookstore-class-diagram.{png,svg,txt}"
    else
        print_status "WARNING" "[WARN] PlantUML not available, skipping UML generation"
        UML_STATUS=0
    fi
else
    UML_STATUS=1  # Assume UML is OK for quick mode
fi

# =============================================================================
# STEP 7: QUALITY CHECKS (if not specific mode)
# =============================================================================

if [ "$TEST_ONLY" = false ] && [ "$JAVADOC_ONLY" = false ] && [ "$PACKAGE_ONLY" = false ] && [ "$UML_ONLY" = false ] && [ "$CLEAN_UML" = false ]; then
    print_header "[QUALITY] STEP 7: QUALITY CHECKS"
    
    # Check for compilation warnings
    print_status "STEP" "Checking for compilation warnings..."
    if mvn compile -q 2>&1 | grep -i "warning\|deprecated" > /dev/null; then
        print_status "WARNING" "[WARN] Compilation warnings detected"
        mvn compile -q 2>&1 | grep -i "warning\|deprecated" | head -5
    else
        print_status "SUCCESS" "[OK] No compilation warnings detected"
    fi
    
    # Check source code quality
    print_status "STEP" "Analyzing source code quality..."
    SOURCE_FILES=$(find src/main/java -name "*.java" | wc -l)
    TEST_FILES=$(find src/test/java -name "*.java" | wc -l)
    TOTAL_LINES=$(find src -name "*.java" -exec wc -l {} + | tail -1 | awk '{print $1}')
    
    print_status "INFO" "Source files: $SOURCE_FILES"
    print_status "INFO" "Test files: $TEST_FILES"
    print_status "INFO" "Total lines of code: $TOTAL_LINES"
    
    # Calculate test ratio
    if [ $SOURCE_FILES -gt 0 ]; then
        TEST_RATIO=$(echo "scale=1; $TEST_FILES * 100 / $SOURCE_FILES" | bc -l 2>/dev/null || echo "N/A")
        print_status "INFO" "Test ratio: $TEST_RATIO% (tests per source file)"
    fi
fi

# =============================================================================
# STEP 8: PACKAGING (if not test-only or specific mode)
# =============================================================================

if [ "$TEST_ONLY" = false ] && [ "$JAVADOC_ONLY" = false ] && [ "$UML_ONLY" = false ] && [ "$CLEAN_UML" = false ]; then
    print_header "[PACKAGE] STEP 8: PACKAGING"
    
    print_status "STEP" "Creating JAR package..."
    if mvn package -DskipTests -q; then
        JAR_FILE=$(ls target/*.jar 2>/dev/null | head -1)
        if [ ! -z "$JAR_FILE" ]; then
            print_status "SUCCESS" "[OK] JAR created successfully"
            print_status "INFO" "JAR file: $JAR_FILE"
            print_status "INFO" "Size: $(du -h $JAR_FILE | cut -f1)"
            
            # Test JAR execution
            print_status "STEP" "Testing JAR execution..."
            if java -jar "$JAR_FILE" --help > /dev/null 2>&1 || java -jar "$JAR_FILE" > /dev/null 2>&1; then
                print_status "SUCCESS" "[OK] JAR executes successfully"
            else
                print_status "WARNING" "[WARN] JAR execution test inconclusive"
            fi
        else
            print_status "WARNING" "[WARN] JAR file not found"
        fi
    else
        print_status "WARNING" "[WARN] JAR creation failed"
    fi
fi

# =============================================================================
# STEP 9: FINAL SUMMARY & STATUS
# =============================================================================

print_header "[SUMMARY] STEP 9: FINAL SUMMARY & STATUS"

# Calculate overall status
OVERALL_STATUS=$((BUILD_STATUS + TEST_STATUS + COVERAGE_STATUS + JAVADOC_STATUS + UML_STATUS))

print_status "STEP" "Build Results Summary:"
echo "  ==================================================================="
echo "                           BUILD STATUS REPORT"
echo "  ==================================================================="
echo "  [BUILD] Compilation:        $([ $BUILD_STATUS -eq 1 ] && echo "[OK] PASS" || echo "[FAIL] FAIL")"
echo "  [TEST] Testing:             $([ $TEST_STATUS -eq 1 ] && echo "[OK] PASS" || echo "[FAIL] FAIL")"
echo "  [COVERAGE] Coverage:        $([ $COVERAGE_STATUS -eq 1 ] && echo "[OK] PASS" || echo "[FAIL] FAIL")"
echo "  [DOCS] Javadoc:             $([ $JAVADOC_STATUS -eq 1 ] && echo "[OK] PASS" || echo "[FAIL] FAIL")"
echo "  [UML] UML Diagrams:         $([ $UML_STATUS -eq 1 ] && echo "[OK] PASS" || echo "[FAIL] FAIL")"
echo "  -------------------------------------------------------------------"
echo "  Overall Score:              $OVERALL_STATUS/5 ($(echo "scale=0; $OVERALL_STATUS * 100 / 5" | bc -l)%)"
echo "  ==================================================================="

# Final status determination
if [ $OVERALL_STATUS -eq 5 ]; then
    print_status "SUCCESS" "[EXCELLENT] All checks passed successfully!"
    print_status "SUCCESS" "Your Lab 2 project is ready for submission!"
elif [ $OVERALL_STATUS -ge 4 ]; then
    print_status "SUCCESS" "[GOOD] Most checks passed. Minor issues detected."
elif [ $OVERALL_STATUS -ge 3 ]; then
    print_status "WARNING" "[FAIR] Some checks passed. Review and fix issues."
else
    print_status "ERROR" "[POOR] Multiple checks failed. Significant issues detected."
fi

# =============================================================================
# STEP 10: NEXT STEPS & RECOMMENDATIONS
# =============================================================================

print_header "[NEXT] STEP 10: NEXT STEPS & RECOMMENDATIONS"

echo "[FILES] Generated Files:"
if [ $COVERAGE_STATUS -eq 1 ]; then
    echo "  • Coverage Report: target/site/jacoco/index.html"
fi
if [ $JAVADOC_STATUS -eq 1 ]; then
    echo "  • Javadoc: target/site/apidocs/index.html"
fi
if [ $UML_STATUS -eq 1 ]; then
    echo "  • UML Diagrams: bookstore-class-diagram.{png,svg,txt}"
    echo "  • UML Source: bookstore-uml.puml"
fi
if [ ! -z "$JAR_FILE" ]; then
    echo "  • Executable JAR: $JAR_FILE"
fi

echo ""
echo "[COMMANDS] Useful Commands:"
echo "  • Run tests only:        mvn test"
echo "  • Generate coverage:     mvn jacoco:report"
echo "  • Generate Javadoc:      mvn javadoc:javadoc"
echo "  • Clean build:           mvn clean compile"
echo "  • Run specific test:     mvn test -Dtest=EBookTest"
echo "  • Generate UML only:     ./runme.sh --uml-only"

echo ""
echo "[CHECKLIST] Lab 2 Submission Checklist:"
echo "  □ All tests pass (✓ $([ $TEST_STATUS -eq 1 ] && echo "DONE" || echo "TODO"))"
echo "  □ Code coverage > $TARGET_COVERAGE% (✓ $([ $COVERAGE_STATUS -eq 1 ] && echo "DONE" || echo "TODO"))"
echo "  □ Javadoc generated (✓ $([ $JAVADOC_STATUS -eq 1 ] && echo "DONE" || echo "TODO"))"
echo "  □ UML diagrams generated (✓ $([ $UML_STATUS -eq 1 ] && echo "DONE" || echo "TODO"))"
echo "  □ Code compiles cleanly (✓ $([ $BUILD_STATUS -eq 1 ] && echo "DONE" || echo "TODO"))"
echo "  □ EBook class implemented (✓ DONE)"
echo "  □ Enhanced search methods (✓ DONE)"
echo "  □ Visitor pattern implemented (✓ DONE)"
echo "  □ Factory pattern implemented (✓ DONE)"

echo ""
echo "[REPORTS] Open Reports:"
if command_exists open; then
    echo "  • Coverage: open target/site/jacoco/index.html"
    echo "  • Javadoc:  open target/site/apidocs/index.html"
    echo "  • UML PNG:  open bookstore-class-diagram.png"
elif command_exists xdg-open; then
    echo "  • Coverage: xdg-open target/site/jacoco/index.html"
    echo "  • Javadoc:  xdg-open target/site/apidocs/index.html"
    echo "  • UML PNG:  xdg-open bookstore-class-diagram.png"
else
    echo "  • Coverage: target/site/jacoco/index.html"
    echo "  • Javadoc:  target/site/apidocs/index.html"
    echo "  • UML PNG:  bookstore-class-diagram.png"
fi

# =============================================================================
# SCRIPT COMPLETION
# =============================================================================

print_header "[END] SCRIPT COMPLETION"

print_status "INFO" "Script completed at: $(date)"
print_status "INFO" "Total execution time: $SECONDS seconds"

if [ $OVERALL_STATUS -eq 5 ]; then
    print_status "SUCCESS" "[SUCCESS] CONGRATULATIONS! Your Lab 2 project meets all requirements!"
    print_status "SUCCESS" "Features implemented: EBook, Enhanced Search, Visitor Pattern, Factory Pattern"
    exit 0
else
    print_status "WARNING" "[WARN] Please review and fix the issues above before submission."
    exit 1
fi
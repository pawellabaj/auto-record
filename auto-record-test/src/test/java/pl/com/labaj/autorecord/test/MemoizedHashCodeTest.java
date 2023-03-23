package pl.com.labaj.autorecord.test;

public class MemoizedHashCodeTest {
    /*
    @AutoRecord(withBuilder = false)
    public interface HashCodeMemoized {
        pl.com.labaj.autorecord.test.HashCodeMemoized.ClassReturningGrowingHashCode used();

        @Ignored
        pl.com.labaj.autorecord.test.HashCodeMemoized.ClassThrowingExceptionFromHashCode ignored();

        class ClassReturningGrowingHashCode {
            private final AtomicInteger hash = new AtomicInteger(0);

            @Override
            public int hashCode() {
                return hash.getAndIncrement();
            }
        }

        class ClassThrowingExceptionFromHashCode {
            @Override
            public int hashCode() {
                throw new RuntimeException("This method should not be called");
            }
        }
    }
     */

//    @Test
//    void shouldIgnoreProperty() {
//        //given
//        var objectReturningGrowingHashCode = new HashCodeMemoized.ClassReturningGrowingHashCode();
//        var objectThrowingExceptionFromHashCode = new HashCodeMemoized.ClassThrowingExceptionFromHashCode();
//
//        var record = new MemoizedHashCodeDataRecord(objectReturningGrowingHashCode, objectThrowingExceptionFromHashCode);
//
//        //when
//        var firstHashCode = record.hashCode();
//
//        var assertions = IntStream.range(0, 10)
//                .map(i -> record.hashCode())
//                .mapToObj(hash -> (Executable) () -> assertThat(hash).isEqualTo(firstHashCode));
//
//        // then
//        assertAll(assertions);
//    }
}

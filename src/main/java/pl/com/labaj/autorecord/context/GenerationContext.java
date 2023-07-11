package pl.com.labaj.autorecord.context;

import io.soabase.recordbuilder.core.RecordBuilder;
import pl.com.labaj.autorecord.AutoRecord;
import pl.com.labaj.autorecord.processor.context.Memoization;
import pl.com.labaj.autorecord.processor.context.SpecialMethod;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Map;

public interface GenerationContext {
    String packageName();

    AutoRecord.Options recordOptions();

    RecordBuilder.Options builderOptions();

    boolean isRecordPublic();

    TypeMirror interfaceType();

    String interfaceName();

    List<RecordComponent> components();

    List<TypeParameterElement> typeParameters();

    Map<SpecialMethod, ExecutableElement> specialMethods();

    Memoization memoization();

    String recordName();

    Logger logger();
}

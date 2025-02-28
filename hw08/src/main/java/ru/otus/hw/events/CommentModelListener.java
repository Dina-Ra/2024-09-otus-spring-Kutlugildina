package ru.otus.hw.events;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Comment;
import ru.otus.hw.sequencegenerator.SequenceGeneratorService;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class CommentModelListener extends AbstractMongoEventListener<Comment> {
    private final SequenceGeneratorService sequenceGenerator;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Comment> event) {
        Long idLongValue = Optional.ofNullable(event.getSource().getId())
                .map(Long::valueOf)
                .orElse(0L);
        if (idLongValue < 1) {
            event.getSource().setId(sequenceGenerator.generateSequence(Comment.SEQUENCE_NAME));
        }
    }
}

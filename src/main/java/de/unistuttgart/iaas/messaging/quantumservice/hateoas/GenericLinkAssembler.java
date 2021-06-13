package de.unistuttgart.iaas.messaging.quantumservice.hateoas;

import java.util.Collection;
import java.util.stream.Collectors;

import de.unistuttgart.iaas.messaging.quantumservice.utils.ModelMapperUtils;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

public abstract class GenericLinkAssembler<T> {

    public abstract void addLinks(EntityModel<T> resource);

    public void addLinks(CollectionModel<EntityModel<T>> resources) {
        for (EntityModel<T> entity : resources.getContent()) {
            addLinks(entity);
        }
    }

    public T getContent(EntityModel<T> resource) {
        return resource.getContent();
    }

    public <U> EntityModel<T> toModel(U entity, Class<T> entityClass) {
        EntityModel<T> entityModel = EntityModel.of(ModelMapperUtils.convert(entity, entityClass));
        addLinks(entityModel);
        return entityModel;
    }

    public <U> CollectionModel<EntityModel<T>> toModel(Collection<U> collection, Class<T> entityClass) {
        final var entities = collection.stream().map(item ->
                EntityModel.of(ModelMapperUtils.convert(item, entityClass))).collect(Collectors.toUnmodifiableList());
        final var collectionModel = CollectionModel.of(entities);
        addLinks(collectionModel);
        return collectionModel;
    }
}

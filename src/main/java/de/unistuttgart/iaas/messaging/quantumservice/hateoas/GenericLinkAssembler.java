package de.unistuttgart.iaas.messaging.quantumservice.hateoas;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.swing.text.html.parser.Entity;

import de.unistuttgart.iaas.messaging.quantumservice.utils.ModelMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

public abstract class GenericLinkAssembler<T> {

    @Autowired
    private PagedResourcesAssembler<T> pagedResourcesAssembler;

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

    public <U> PagedModel<EntityModel<T>> toModel(Page<U> page, Class<T> entityClass) {
        final var entities = page.map(item -> ModelMapperUtils.convert(item, entityClass));
        final var model = pagedResourcesAssembler.toModel(entities);

        for (EntityModel<T> entity: model.getContent()) {
            addLinks(entity);
        }

        return model;
    }

    public <U> CollectionModel<EntityModel<T>> toModel(Collection<U> collection, Class<T> entityClass) {
        final var entities = collection.stream().map(item ->
                EntityModel.of(ModelMapperUtils.convert(item, entityClass))).collect(Collectors.toUnmodifiableList());
        final var collectionModel = CollectionModel.of(entities);
        addLinks(collectionModel);
        return collectionModel;
    }
}

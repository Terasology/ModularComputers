/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.movingBlock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.asset.Assets;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.lifecycleEvents.BeforeDeactivateComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.rendering.assets.mesh.Mesh;
import org.terasology.rendering.logic.MeshComponent;
import org.terasology.rendering.nui.Color;
import org.terasology.world.block.Block;

@RegisterSystem(RegisterMode.CLIENT)
public class MovingBlockClientSystem extends BaseComponentSystem implements UpdateSubscriberSystem {
    private static final Logger logger = LoggerFactory.getLogger(MovingBlockClientSystem.class);

    @In
    private EntityManager entityManager;
    @In
    private Time time;

    @Override
    public void update(float delta) {
        long gameTimeInMs = time.getGameTimeInMs();
        for (EntityRef entityRef : entityManager.getEntitiesWith(MovingBlockComponent.class, LocationComponent.class, MeshComponent.class)) {
            MovingBlockComponent movingBlock = entityRef.getComponent(MovingBlockComponent.class);

            Vector3i locationFrom = movingBlock.getLocationFrom();
            Vector3i locationTo = movingBlock.getLocationTo();

            long timeStart = movingBlock.getTimeStart();
            long timeEnd = movingBlock.getTimeEnd();

            Vector3f result;
            if (gameTimeInMs <= timeStart) {
                result = locationFrom.toVector3f();
            } else if (gameTimeInMs >= timeEnd) {
                result = locationTo.toVector3f();
            } else {
                float resultDiff = 1f * (gameTimeInMs - timeStart) / (timeEnd - timeStart);
                result = new Vector3f(
                        locationFrom.x + resultDiff * (locationTo.x - locationFrom.x),
                        locationFrom.y + resultDiff * (locationTo.y - locationFrom.y),
                        locationFrom.z + resultDiff * (locationTo.z - locationFrom.z));
            }

            LocationComponent location = entityRef.getComponent(LocationComponent.class);
            location.setWorldPosition(result);
            entityRef.saveComponent(location);
        }
    }

    @ReceiveEvent
    public void movingStarted(OnActivatedComponent event, EntityRef entity, MovingBlockComponent movingBlock, LocationComponent locationComponent) {
        Block blockToRender = movingBlock.getBlockToRender();
        Mesh mesh = blockToRender.getMeshGenerator().getStandaloneMesh();

        MeshComponent meshComponent = new MeshComponent();
        meshComponent.mesh = mesh;
        meshComponent.material = Assets.getMaterial("engine:terrain");
        meshComponent.translucent = false;
        meshComponent.hideFromOwner = false;
        meshComponent.color = Color.WHITE;

        entity.addComponent(meshComponent);
    }

    @ReceiveEvent
    public void movingStopped(BeforeDeactivateComponent event, EntityRef entity, MovingBlockComponent movingBlock, LocationComponent locationComponent) {
        entity.removeComponent(MeshComponent.class);
    }
}

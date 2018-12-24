/*
 * Copyright 2014 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.gelcubes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.rendering.logic.MeshComponent;
import org.terasology.rendering.nui.Color;
import org.terasology.utilities.random.FastRandom;

/**
 * Factory for generating Gelatinous Cubes.
 *
 * @author Rasmus 'Cervator' Praestholm <cervator@gmail.com>
 */
public class GelCubeFactory {

    // TODO: This class is actually just a dump of the old DefaultMobFactory that created either GelCubes or models
    // Engine support for the special hack allowing gelcubes was removed and something needs to be done to support again
    private static final Vector3f[] COLORS =
            {new Vector3f(1.0f, 1.0f, 0.2f),
             new Vector3f(1.0f, 0.2f, 0.2f),
             new Vector3f(0.2f, 1.0f, 0.2f),
             new Vector3f(1.0f, 1.0f, 0.2f)};

    private static final Logger logger = LoggerFactory.getLogger(GelCubeFactory.class);

    /** Random source */
    private FastRandom random;
    /** For getting entities*/
    private EntityManager entityManager;

    /**
     * Creates a prefab in a given position in the world. No current guarantee it'll be a GelCube, needs fixing.
     * @param position Where to create the prefab
     * @param prefab Which prefab to create
     * @return A reference to the entity created
     */
    public EntityRef generate(Vector3f position, Prefab prefab) {
        // Create new prefab
        EntityRef entity = entityManager.create(prefab.getName(), position);

        // TODO: Old GelCubes were found from having a plain MeshComponent. Might need a GelCubeComponent ? Color etc.
        MeshComponent mesh = entity.getComponent(MeshComponent.class);
        if (mesh != null) {
            logger.info("Spawning a supposed GelCube: {}", prefab);
            // For changing location (and size?) - needs to be changed around sometime
            LocationComponent loc = entity.getComponent(LocationComponent.class);
            if (loc != null) {
                loc.setWorldPosition(position);
                loc.setLocalScale((random.nextFloat() + 1.0f) * 0.4f + 0.2f);
                entity.saveComponent(loc);
            }

            logger.info("Creating a {} with color {} - if default/black then will overwrite with a random color", prefab.getName(), mesh.color);
            // For uninitialized (technically black) GelCubes we just come up with a random color. Well, small list. For now.
            if (mesh.color.equals(new Color(0, 0, 0, 1))) {
                int colorId = Math.abs(random.nextInt()) % COLORS.length;
                mesh.color = new Color(COLORS[colorId].x, COLORS[colorId].y, COLORS[colorId].z, 1.0f);
                entity.saveComponent(mesh);
            }
        } else {
            logger.info("Was given a prefab with no mesh, can't spawn :-( {}", prefab);
        }

        return entity;
    }

    public void setRandom(FastRandom random) {
        this.random = random;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}

/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.Arrays;

import org.junit.Test;

import cern.streaming.pool.core.exception.CycleInStreamDiscoveryDetectedException;
import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.ReactStream;
import cern.streaming.pool.core.service.StreamId;

@SuppressWarnings("unchecked")
/**
 * Standard unit tests covering {@link CreatorStreamFactory}
 * 
 * @author mgalilee
 */
public class CreatorStreamFactoryTest {

    private static final StreamId<Object> ID_A = mock(StreamId.class);
    private static final StreamId<Object> ID_B = mock(StreamId.class);
    private static final ReactStream<Object> STREAM_A = mock(ReactStream.class);
    private static final ReactStream<Object> STREAM_B = mock(ReactStream.class);
    
    private final IdentifiedStreamCreator<Object> creator = IdentifiedStreamCreator.of(ID_A, discovery -> STREAM_A);
    private final CreatorStreamFactory factory = new CreatorStreamFactory(Arrays.asList(creator));
    private final DiscoveryService discoveryService = new LazyPool(Arrays.asList(factory));
    
    @Test(expected = CycleInStreamDiscoveryDetectedException.class)
    public void testCycleLoopDetectedUsingStreamCreators() {
        DiscoveryService loopingDiscoveryService = new LazyPool(Arrays.asList(createLoopCreatorStreamFactory()));
        
        loopingDiscoveryService.discover(ID_A);
    }
    
    @Test
    public void createUnavailableStream() {
        ReactStream<?> stream = factory.create(new NamedStreamId<>("mysterystream"), discoveryService);
        
        assertEquals(null, stream);
    }
    
    @Test
    public void createAvailableStream() {
        ReactStream<?> stream = factory.create(ID_A, discoveryService);
        
        assertEquals(STREAM_A, stream);
    }
    
    @Test(expected = NullPointerException.class)
    public void provideWithNullId() {
        factory.provide(null, null);
    }
    
    @Test(expected = NullPointerException.class)
    public void provideWithNullSupplier() {
        factory.provide(ID_A, null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void provideDuplicateSupplier() {
        factory.provide(ID_A, creator.getCreator());
    }
    
    @Test
    public void provideNewSupplier() {
        factory.provide(ID_B, discovery -> STREAM_B);
        ReactStream<?> stream = factory.create(ID_B, discoveryService);
        
        assertEquals(STREAM_B, stream);
    }

    private CreatorStreamFactory createLoopCreatorStreamFactory() {
        IdentifiedStreamCreator<Object> streamCreatorWithLoop = IdentifiedStreamCreator.of(ID_A, discovery -> {
            discovery.discover(ID_A);
            throw new RuntimeException("The test failed, a loop should be detected!");
        });
        return new CreatorStreamFactory(Arrays.asList(streamCreatorWithLoop));
    }
}

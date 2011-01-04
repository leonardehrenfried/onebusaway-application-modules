package org.onebusaway.transit_data_federation.impl.otp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.onebusaway.transit_data_federation.services.transit_graph.BlockConfigurationEntry;
import org.onebusaway.transit_data_federation.services.transit_graph.BlockStopTimeEntry;
import org.onebusaway.transit_data_federation.services.tripplanner.StopTimeInstance;
import org.opentripplanner.routing.core.Edge;
import org.opentripplanner.routing.core.HasEdges;
import org.opentripplanner.routing.core.Vertex;

public class BlockArrivalVertex extends AbstractBlockVertex implements HasEdges {

  public BlockArrivalVertex(GraphContext graphContext, StopTimeInstance instance) {
    super(graphContext, instance);
  }

  /****
   * {@link Vertex} Interface
   ****/

  @Override
  public String getLabel() {
    return "block_arrival: " + _instance.toString();
  }

  /****
   * {@link HasEdges} Interface
   ****/

  @Override
  public int getDegreeIn() {
    return getIncoming().size();
  }

  @Override
  public Collection<Edge> getIncoming() {

    BlockStopTimeEntry bst = _instance.getStopTime();
    BlockConfigurationEntry config = bst.getTrip().getBlockConfiguration();
    List<BlockStopTimeEntry> stopTimes = config.getStopTimes();

    // The assumption is that we would not have been instantiated unless we had
    // a previous stop time to arrive from.
    BlockStopTimeEntry prev = stopTimes.get(bst.getBlockSequence() - 1);
    return Arrays.asList((Edge) new BlockHopEdge(_context, prev, bst,
        _instance.getServiceDate()));
  }

  @Override
  public int getDegreeOut() {
    return getOutgoing().size();
  }

  @Override
  public Collection<Edge> getOutgoing() {

    List<Edge> edges = new ArrayList<Edge>();

    if (SupportLibrary.hasNextStopTime(_instance)) {
      edges.add(new BlockDwellEdge(_context, _instance));
    }

    edges.add(new ArrivalEdge(_context, _instance.getStop()));

    return edges;
  }

  /****
   * {@link Object} Interface
   ****/

  @Override
  public String toString() {
    return "block_arrival: " + _instance.toString();
  }
}
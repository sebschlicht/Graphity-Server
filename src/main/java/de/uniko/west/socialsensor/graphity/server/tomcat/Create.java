package de.uniko.west.socialsensor.graphity.server.tomcat;

import java.util.Queue;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.uniko.west.socialsensor.graphity.socialgraph.operations.CreateFriendship;
import de.uniko.west.socialsensor.graphity.socialgraph.operations.CreateStatusUpdate;
import de.uniko.west.socialsensor.graphity.socialgraph.operations.SocialGraphOperation;
import de.uniko.west.socialsensor.graphity.socialgraph.statusupdates.PlainText;
import de.uniko.west.socialsensor.graphity.socialgraph.statusupdates.StatusUpdate;

/**
 * Tomcat create operation handler
 * 
 * @author Sebastian Schlicht
 * 
 */
@WebServlet("/create")
public class Create extends HttpServlet {

	/**
	 * generated serial version UID
	 */
	private static final long serialVersionUID = 4435622251859808192L;
	/**
	 * command queue to stack commands created
	 */
	private final Queue<SocialGraphOperation> commandQueue;

	/**
	 * create a new Tomcat create operation handler
	 * 
	 * @param commandQueue
	 *            command queue to stack commands created
	 */
	public Create(final Queue<SocialGraphOperation> commandQueue) {
		super();
		this.commandQueue = commandQueue;
	}

	@Override
	protected void doGet(final HttpServletRequest request,
			final HttpServletResponse response) {
		CreateType type;
		try {
			final String identifier = Helper.getString(request, "createType");
			type = CreateType.GetCreateType(identifier);
			if (type == null) {
				throw new ClassCastException();
			}
		} catch (final IllegalArgumentException missing) {
			// send error - field missing
			Helper.sendErrorMessage(
					response,
					400,
					"the field \"createType\" is missing!<br>please specify what you want to create.");
			return;
		} catch (final ClassCastException invalid) {
			// send error - field type invalid
			Helper.sendErrorMessage(
					response,
					400,
					"the field \"createType\" is corrupted!<br>please type in a valid create type identifier.");
			return;
		}

		// TODO: use OAuth
		final long userId = Helper.getLong(request, "user_id");

		if (type == CreateType.FRIENDSHIP) {
			long targetId;
			try {
				targetId = Helper.getLong(request, "targetId");
			} catch (final IllegalArgumentException missing) {
				// send error - field missing
				Helper.sendErrorMessage(
						response,
						400,
						"the field \"targetId\" is missing!<br>please specify the user you want to follow.");
				return;
			} catch (final ClassCastException invalid) {
				// send error - field type invalid
				Helper.sendErrorMessage(
						response,
						400,
						"the field \"targetId\" is corrupted!<br>please provide a number greater than zero.");
				return;
			}

			// TODO: store response item for the server response creation
			final CreateFriendship createFriendshipCommand = new CreateFriendship(
					System.currentTimeMillis(), userId, targetId);
			this.commandQueue.add(createFriendshipCommand);
		} else {
			// TODO: read and check status update type (may be disallowed),
			// changes field to load
			final String message = Helper.getString(request, "message");
			final StatusUpdate statusUpdate = new PlainText(message);

			// TODO: store response item for the server response creation
			final CreateStatusUpdate createStatusUpdateCommand = new CreateStatusUpdate(
					System.currentTimeMillis(), userId, statusUpdate);
			this.commandQueue.add(createStatusUpdateCommand);
		}
	}

}
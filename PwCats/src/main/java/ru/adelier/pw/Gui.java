package ru.adelier.pw;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

import javax.swing.SwingConstants;

import java.awt.SystemColor;
import javax.swing.JTextArea;

public class Gui {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui window = new Gui();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Gui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 453, 225);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		final JTextArea txtrDg = new JTextArea();
		txtrDg.setFont(new Font("Courier New", Font.PLAIN, 11));
		txtrDg.setBackground(SystemColor.menu);
		txtrDg.setTabSize(4);
		txtrDg.setWrapStyleWord(true);
		txtrDg.setText("...");
		txtrDg.setLineWrap(true);
		txtrDg.setBounds(10, 104, 427, 82);
		frame.getContentPane().add(txtrDg);
		
		final JTextPane textPaneQuestId = new JTextPane();
		textPaneQuestId.setToolTipText("id \u043A\u0432\u0435\u0441\u0442\u0430 \u0441\u0443\u043D\u0434\u0443\u043A\u0430 \u043F\u043E \u0431\u0430\u0437\u0435 ( http://www.pwdatabase.com/ru/quest/28842 )");
		textPaneQuestId.setFont(new Font("Courier New", Font.PLAIN, 11));
		textPaneQuestId.setText("28842");
		textPaneQuestId.setBounds(10, 11, 77, 20);
		frame.getContentPane().add(textPaneQuestId);
		
		final JTextPane textPaneServer = new JTextPane();
		textPaneServer.setToolTipText("\u0421\u0435\u0440\u0432\u0435\u0440 \u043F\u043E pwcats ( http://pwcats.info/cats/vega/item/16466 )");
		textPaneServer.setFont(new Font("Courier New", Font.PLAIN, 11));
		textPaneServer.setText("vega");
		textPaneServer.setBounds(10, 73, 77, 20);
		frame.getContentPane().add(textPaneServer);
		
		final JLabel lblNewLabel = new JLabel("\u0426\u0435\u043D\u0430");
		lblNewLabel.setForeground(Color.RED);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Courier New", Font.PLAIN, 20));
		lblNewLabel.setBackground(Color.LIGHT_GRAY);
		lblNewLabel.setBounds(198, 11, 239, 82);
		frame.getContentPane().add(lblNewLabel);
		
		final JTextPane textPaneUnknownPrice = new JTextPane();
		textPaneUnknownPrice.setToolTipText("\u0426\u0435\u043D\u0430 \u043D\u0430 \u043D\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043D\u043E\u0435");
		textPaneUnknownPrice.setText("0");
		textPaneUnknownPrice.setFont(new Font("Courier New", Font.PLAIN, 11));
		textPaneUnknownPrice.setBounds(10, 42, 77, 20);
		frame.getContentPane().add(textPaneUnknownPrice);
		
		JButton btnGo = new JButton("Go");
		btnGo.setFont(new Font("Courier New", Font.PLAIN, 11));
		btnGo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int questid = Integer.parseInt(textPaneQuestId.getText());
					frame.setTitle(PwDBChestQuestRequester.requestQuestName(questid));
					
					//requestName
					PwDBChestQuestRequester requester = new PwDBChestQuestRequester();
					requester.getTotalChestPrice(textPaneServer.getText(), 
							questid, 
							Integer.parseInt(textPaneUnknownPrice.getText()));
					float price = requester.getPrice();
					lblNewLabel.setText(PwDBChestQuestRequester.formatPwPrice((int) price));
					
					List<String> unknown = requester.getUnknownPricesItems();
					if (!unknown.isEmpty())
						txtrDg.setText(String.format("Цены на %s не найдены на pwcats, и установлены %d",
								unknown, requester.getUnknownPrice()));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		btnGo.setBounds(103, 8, 85, 85);
		frame.getContentPane().add(btnGo);
	}
}

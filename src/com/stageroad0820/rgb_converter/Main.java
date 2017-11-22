package com.stageroad0820.rgb_converter;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("serial")
public class Main extends JFrame{
	String filePath = null;
	String fileName = null;
	
	public void onClickLoad() {
		JFileChooser load = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("사용 가능한 이미지 파일 ", "jpg", "jpeg", "png", "bmp");
		File file = new File(System.getenv("USERPROFILE") + "\\Pictures");
		
		load.setFileFilter(filter);
		load.setDialogTitle("사진 파일 불러오기");
		load.setPreferredSize(new Dimension(800, 450));
		load.setCurrentDirectory(file);
		
		System.out.println("[Log:Info] 파일 열기 다이얼로그를 실행합니다. 실행 위치: " + load.getCurrentDirectory());
		
		int event = load.showOpenDialog(this);
		
		if (event == JFileChooser.APPROVE_OPTION) {
			System.out.println("[Log:Info] 파일을 선택했습니다. 파일 위치: " + load.getSelectedFile().getPath());
			
			int confirm = JOptionPane.showConfirmDialog(null
					, "다음 파일을 열려고 합니다. 맞는지 확인해 주세요.\n\n- 파일 이름: " + load.getSelectedFile().getName() + "\n- 파일 위치: " + load.getSelectedFile().getPath()
					, "파일 확인"
					, JOptionPane.OK_CANCEL_OPTION);
			
			if (confirm == JOptionPane.OK_OPTION) {
				System.out.println("[Log:Info] 파일 정보를 수집하고 있습니다.");
				
				String filePath = load.getSelectedFile().getPath();
				String fileName = load.getSelectedFile().getName();
				
				try {
					System.out.println("[Log:Info] 파일 정보가 수집되었습니다. 다음 단계로 진행합니다.");
					
					this.getImage(filePath, fileName);
				} catch (IOException e) {
					e.printStackTrace();
					
					System.err.println("[Log:Error] 파일 정보를 수집하여 다음 단계로 진행하는 도중 예외가 발생하였습니다: " + e.getMessage() + "/" + e.getCause());
				}
			}
			else if (confirm == JOptionPane.CANCEL_OPTION) {
				System.out.println("[Log:Info] 파일 열기가 확인 단계에서 취소되었습니다. 다이얼로그를 다시 엽니다.");
				
				this.onClickLoad();
			}
			else {
				System.err.println("[Log:Error] 에러가 발생하였습니다. 소스코드의 다음 줄을 확인해 주세요: " + this.getLineNumber());
			}
		}
		else if (event == JFileChooser.CANCEL_OPTION) {
			System.out.println("[Log:Info] 파일 열기가 파일 선택 단계에서 취소되었습니다.");
			
			msgbox("error", "사용자 취소", "파일 열기를 취소하셨습니다. 파일을 열려면 [사진 불러오기] 버튼을 눌러주세요.");
		}
		else {
			System.err.println("[Log:Error] 에러가 발생하였습니다. 소스코드의 다음 줄을 확인해 주세요: " + this.getLineNumber());
		}
	}
	
	public void getImage(String filePath, String fileName) throws IOException {
		try {
			System.out.println("[Log:Info] 파일 정보를 받았습니다. 출력에 사용될 파일을 준비하는 중 입니다...");
			
			File file = new File(filePath);
			BufferedImage bf_img = ImageIO.read(file);
			FileWriter output = new FileWriter("rgb_converter-" + fileName + ".txt");
			
			System.out.println("[Log:Info] 출력에 사용될 파일은 다음과 같습니다: rgb_converter-" + fileName + ".txt");
			
			Desktop dt = Desktop.getDesktop();
			
			int height = bf_img.getHeight();
			int width = bf_img.getWidth();
			
			System.out.println("[Log:Info] 파일의 세부 정보를 수집했습니다. 높이: " + height + ", 너비: " + width);
			
			int red, green, blue, pixel;
			
			int confirm = JOptionPane.showConfirmDialog(null
					, "파일 입력을 시작하기 전 아래의 내용을 확인하시고 진행해 주세요.\n파일의 크기가 클 수록 파일 입력은 오래걸립니다.\n\n- 이미지 높이: " + height + " px\n- 이미지 너비: " + width + " px"
					, "파일 입력 시작 - 사용자 확인"
					, JOptionPane.OK_CANCEL_OPTION);
			
			if (confirm == JOptionPane.OK_OPTION) {
				System.out.println("[Log:Info] 파일 입력을 시작합니다. 파일 크기에 따라 시간이 다소 걸릴 수 있습니다.");
				
				msgbox("info", "파일 입력 시작", "파일 입력을 시작합니다. 파일 크기에 따라 시간이 다소 걸릴 수 있습니다.");
				
				output.write("Red,Green,Blue");
				
				for (int i = 0; i < height; i++) {
					for (int j = 0; j < width; j++) {
						pixel = bf_img.getRGB(j, i);
						red = (pixel >> 16) & 0xff;
						green = (pixel >> 8) & 0xff;
						blue = pixel & 0xff;
						
						String contents = red + "," + green + "," + blue + "\r\n";
						System.out.println("[Log:Info] 파일 입력 중 입니다. 현재 픽셀: " + j + ", " + i + " / RGB 값: [" + red + ", " + green + ", " + blue +"]");
						output.write(contents);
					}
				}
				
				output.close();
				
				System.out.println("[Log:Info] 파일 입력이 완료되었습니다.");
				
				msgbox("info", "파일 입력 성공", "파일 입력이 완료되었습니다. 파일 이름은 다음과 같습니다.\n\n- 파일 이름: rgb_converter-" + fileName + ".txt");
				
				File temp = new File("rgb_converter-" + fileName + ".txt");
				
				System.out.println("[Log:Info] 파일을 엽니다. 파일 위치: " + file.getPath());
				
				dt.open(temp);
			}
			else if (confirm == JOptionPane.CANCEL_OPTION) {
				System.out.println("[Log:Info] 사용자가 파일 입력을 취소하였습니다. 모든 내용을 초기화 합니다.");
				
				msgbox("error", "파일 입력 실패 - 사용자 취소", "파일 입력을 취소하셨습니다. 파일을 다시 열어주세요.");
			}
			else {
				System.err.println("[Log:Error] 에러가 발생하였습니다. 소스코드의 다음 줄을 확인해 주세요: " + this.getLineNumber());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void msgbox(String type, String title, String message) {
		if (type.equalsIgnoreCase("error")) {
			JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		}
		else if (type.equalsIgnoreCase("info")) {
			JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			System.err.println("[Log:Error] 팝업 메세지 상자를 만드는 도중 오류가 발생하였습니다. 사유: 잘못된 메세지 상자 타입 입니다.");
		}
	}
	
	public static void main(String[] args) {
		Main mainsc = new Main();
		JFrame fmain = new JFrame();
		JButton btn_load = new JButton();
		JPanel pmain = new JPanel();
		
		fmain.setTitle("RGB Converter v0.1.1 [Beta]");
		fmain.setSize(800, 450);
		
		btn_load.setText("사진 불러오기");
		btn_load.setSize(200, 112);
		
		btn_load.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					mainsc.onClickLoad();
					System.out.println("[Log:Info] '사진 불러오기' 버튼 이벤트가 처리되었습니다.");
				} catch (Exception e1) {
					e1.printStackTrace();
					
					System.err.println("[Log:Error] '사진 불러오기' 버튼 이벤트를 처리하는 도중 예외가 발생하였습니다: " + e1.getMessage() + "/" + e1.getCause());
				}
			}
		});
		
		pmain.add(btn_load);
		
		fmain.add(pmain);
		fmain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fmain.setVisible(true);
		
		System.out.println("[Log:Info] JFrame 화면 " + fmain.getTitle() + " (을)를 실행, 표시합니다.");
	}
	
	private int getLineNumber() {
	    return Thread.currentThread().getStackTrace()[2].getLineNumber();
	}
}